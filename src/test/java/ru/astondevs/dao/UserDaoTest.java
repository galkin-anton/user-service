package ru.astondevs.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.UserEntity;
import ru.astondevs.mapper.UserMapper;
import ru.astondevs.util.HibernateProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {

    HibernateProvider hibernateProvider;

    @Mock
    UserMapper userMapperMock;

    @Container
    private final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17")
        .withDatabaseName("test_db")
        .withUsername("test_user")
        .withPassword("test_pass");

    UserDao underTest;

    @BeforeAll
    void setUp() {
        container.start();

        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.setProperty("hibernate.connection.url", container.getJdbcUrl());
        properties.setProperty("hibernate.connection.username", container.getUsername());
        properties.setProperty("hibernate.connection.password", container.getPassword());
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop"); // Для тестов: создаёт и удаляет таблицы
        properties.setProperty("hibernate.show_sql", "true");

        hibernateProvider = new HibernateProvider(properties);
    }

    @BeforeEach
    void init() {
        underTest = new UserDao(hibernateProvider.getSession(), userMapperMock);
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }

    @Test
    void create() {
        UserDto userDto = new UserDto();
        UserEntity userEntity = new UserEntity("test_user", "test@test.com", 32);
        when(userMapperMock.toEntity(userDto)).thenReturn(userEntity);

        underTest.create(userDto);
    }

    @Test
    public void read() {
        UserDto foundUser = underTest.read(1);
        assertNotNull(foundUser);
        assertEquals("test_user", foundUser.getName());
        assertEquals("test@test.com", foundUser.getEmail());
        assertEquals(32, foundUser.getAge());
    }

    @Test
    public void update() {
        UserDto userDto = underTest.read(1);
        assertNotNull(userDto);

        userDto.setName("test_user2");
        userDto.setEmail("test2@test.com");
        userDto.setAge(48);

        underTest.update(userDto);

        UserDto updatedUser = underTest.read(1);
        assertNotNull(updatedUser);
        assertEquals("test_user2", updatedUser.getName());
        assertEquals("test2@test.com", updatedUser.getEmail());
        assertEquals(48, updatedUser.getAge());
    }

    @Test
    public void getAll() {
        UserDto newUser = new UserDto();
        newUser.setName("test_user3");
        newUser.setEmail("test3@test.com");
        newUser.setAge(25);
        newUser.setCreatedAt(LocalDateTime.now());
        underTest.create(newUser);

        List<UserDto> users = underTest.getAll();
        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
    }

    @Test
    public void delete() {
        underTest.delete(1);
        assertNull(underTest.read(1));
    }
}

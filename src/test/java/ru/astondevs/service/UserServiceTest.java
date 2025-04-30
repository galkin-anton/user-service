package ru.astondevs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDto;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    PipedOutputStream pipedOutputStream = new PipedOutputStream();
    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

    @Captor
    ArgumentCaptor<UserDto> userDtoArgumentCaptor;

    @Mock
    UserDao userDao;

    UserService underTest;

    UserServiceTest() throws IOException {
    }

    @BeforeEach
    void beforeEach() {
        underTest = new UserService(pipedInputStream, userDao);
    }

    @Test
    void saveUser() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@email.com");
        userDto.setAge(35);

        Thread manageThread = new Thread(() -> underTest.manage());
        manageThread.start();
        pipedOutputStream.write("1\n".getBytes(UTF_8));
        pipedOutputStream.write((userDto.getName() + "\n").getBytes(UTF_8));
        pipedOutputStream.write((userDto.getEmail() + "\n").getBytes(UTF_8));
        pipedOutputStream.write((userDto.getAge() + "\n").getBytes(UTF_8));
        pipedOutputStream.write("6\n".getBytes(UTF_8));
        pipedOutputStream.close();
        manageThread.join(Duration.ofSeconds(10));

        verify(userDao).create(userDtoArgumentCaptor.capture());
        assertAll(
            () -> assertEquals(userDto.getName(), userDtoArgumentCaptor.getValue().getName()),
            () -> assertEquals(userDto.getAge(), userDtoArgumentCaptor.getValue().getAge()),
            () -> assertEquals(userDto.getEmail(), userDtoArgumentCaptor.getValue().getEmail())
        );
    }
}
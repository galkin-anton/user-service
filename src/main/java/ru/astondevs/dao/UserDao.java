package ru.astondevs.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.UserEntity;
import ru.astondevs.mapper.UserMapper;

import java.util.List;

public class UserDao {

    private final Session session;
    private final UserMapper mapper;

    public UserDao(Session session, UserMapper mapper) {
        this.session = session;
        this.mapper = mapper;
    }

    // Создание
    public void create(UserDto userDto) {
        UserEntity entity = mapper.toEntity(userDto);
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Чтение по ID
    public UserDto read(int id) {
        UserEntity entity = session.get(UserEntity.class, id);
        return entity != null ? mapper.toDto(entity) : null;
    }

    // Обновление
    public void update(UserDto userDto) {
        UserEntity entity = mapper.toEntity(userDto);
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Удаление
    public void delete(int id) {
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            UserEntity entity = session.get(UserEntity.class, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Получение всех
    public List<UserDto> getAll() {
        List<UserEntity> entities = session.createQuery("FROM UserEntity", UserEntity.class).list();
        session.close();
        return entities.stream()
            .map(mapper::toDto)
            .toList();
    }
}
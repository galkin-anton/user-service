package ru.astondevs;

import ru.astondevs.dao.UserDao;
import ru.astondevs.mapper.UserMapper;
import ru.astondevs.service.UserService;
import ru.astondevs.util.HibernateProvider;

public class Main {

    public static void main(String[] args) {
        try (HibernateProvider hibernateProvider = new HibernateProvider()) {
            new UserService(
                System.in,
                new UserDao(hibernateProvider.getSession(), new UserMapper())
            )
                .manage();
        }
    }
}
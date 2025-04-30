package ru.astondevs.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.astondevs.entity.UserEntity;

import java.util.Properties;

public class HibernateProvider implements AutoCloseable {

    private final Configuration configuration;
    private final SessionFactory sessionFactory;
    private final Session session;

    public HibernateProvider(Properties properties) {
        this.configuration = new Configuration();
        if (properties != null) {
            properties.forEach((key, value) -> configuration.setProperty(key.toString(), value.toString()));
        }

        try {
            // Регистрируем классы сущностей

            configuration.addAnnotatedClass(UserEntity.class);

            // Строим SessionFactory
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            this.session = sessionFactory.openSession();
        } catch (Throwable ex) {
            System.err.println("Ошибка при создании SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public HibernateProvider() {
        this(null);
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
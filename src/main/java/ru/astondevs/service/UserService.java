package ru.astondevs.service;

import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDto;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserService {

    private final InputStream source;
    private final UserDao userDao;

    public UserService(InputStream source, UserDao userDao) {
        this.source = source;
        this.userDao = userDao;
    }

    public void manage() {
        Scanner scanner = new Scanner(source);
        while (true) {
            System.out.println("\nВыберите операцию:");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Прочитать пользователя по ID");
            System.out.println("3. Обновить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Получить всех пользователей");
            System.out.println("6. Выйти");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createUser(scanner, userDao);
                    break;
                case 2:
                    readUser(scanner, userDao);
                    break;
                case 3:
                    updateUser(scanner, userDao);
                    break;
                case 4:
                    deleteUser(scanner, userDao);
                    break;
                case 5:
                    getAllUsers(userDao);
                    break;
                case 6:
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createUser(Scanner scanner, UserDao userDao) {
        System.out.println("Введите имя:");
        String name = scanner.nextLine();
        System.out.println("Введите email:");
        String email = scanner.nextLine();
        System.out.println("Введите возраст:");
        int age = scanner.nextInt();

        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setAge(age);
        userDto.setCreatedAt(LocalDateTime.now());

        userDao.create(userDto);
        System.out.println("Пользователь успешно создан!");
    }

    private void readUser(Scanner scanner, UserDao userDao) {
        System.out.println("Введите ID пользователя:");
        int id = scanner.nextInt();

        UserDto userDto = userDao.read(id);
        if (userDto != null) {
            System.out.println("ID: " + userDto.getId());
            System.out.println("Имя: " + userDto.getName());
            System.out.println("Email: " + userDto.getEmail());
            System.out.println("Возраст: " + userDto.getAge());
            System.out.println("Дата создания: " + userDto.getCreatedAt());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private void updateUser(Scanner scanner, UserDao userDao) {
        System.out.println("Введите ID пользователя:");
        int id = scanner.nextInt();
        scanner.nextLine();

        UserDto userDto = userDao.read(id);
        if (userDto == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        System.out.println("Введите новое имя (текущее: " + userDto.getName() + "):");
        String name = scanner.nextLine();
        System.out.println("Введите новый email (текущий: " + userDto.getEmail() + "):");
        String email = scanner.nextLine();
        System.out.println("Введите новый возраст (текущий: " + userDto.getAge() + "):");
        int age = scanner.nextInt();

        userDto.setName(name.isEmpty() ? userDto.getName() : name);
        userDto.setEmail(email.isEmpty() ? userDto.getEmail() : email);
        userDto.setAge(age);

        userDao.update(userDto);
        System.out.println("Пользователь успешно обновлен!");
    }

    private void deleteUser(Scanner scanner, UserDao userDao) {
        System.out.println("Введите ID пользователя:");
        int id = scanner.nextInt();

        userDao.delete(id);
        System.out.println("Пользователь успешно удален!");
    }

    private void getAllUsers(UserDao userDao) {
        List<UserDto> users = userDao.getAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            for (UserDto userDto : users) {
                System.out.println("ID: " + userDto.getId() + ", Имя: " + userDto.getName() +
                                   ", Email: " + userDto.getEmail() + ", Возраст: " + userDto.getAge() +
                                   ", Дата создания: " + userDto.getCreatedAt());
            }
        }
    }

}

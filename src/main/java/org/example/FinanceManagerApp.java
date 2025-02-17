package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceManagerApp {

    // Глобальные переменные
    static Map<String, User> usersData = new HashMap<>();  // username -> User
    static String currentUser = null;  // имя текущего авторизованного пользователя
    static final String DATA_FILE = "users_data.json";

    public static void main(String[] args) {
        loadDataFromFile();

        System.out.println("Добро пожаловать в систему управления личными финансами (Java)!");
        mainLoop();

        saveDataToFile();
        System.out.println("Программа завершена.");
    }

    private static void mainLoop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nВведите команду (help для справки): ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "register":
                    registerUser(scanner);
                    break;
                case "login":
                    loginUser(scanner);
                    break;
                case "income":
                    if (currentUser == null) {
                        System.out.println("Сначала авторизуйтесь (команда login).");
                    } else {
                        addIncome(scanner);
                    }
                    break;
                case "expense":
                    if (currentUser == null) {
                        System.out.println("Сначала авторизуйтесь (команда login).");
                    } else {
                        addExpense(scanner);
                    }
                    break;
                case "budget":
                    if (currentUser == null) {
                        System.out.println("Сначала авторизуйтесь (команда login).");
                    } else {
                        setBudget(scanner);
                    }
                    break;
                case "stats":
                    if (currentUser == null) {
                        System.out.println("Сначала авторизуйтесь (команда login).");
                    } else {
                        showStats();
                    }
                    break;
                case "exit":
                    System.out.println("Сохранение данных и выход...");
                    return; // Завершение цикла
                default:
                    System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("\nДоступные команды:");
        System.out.println("  register  - Регистрация нового пользователя");
        System.out.println("  login     - Авторизация");
        System.out.println("  income    - Добавить доход");
        System.out.println("  expense   - Добавить расход");
        System.out.println("  budget    - Установить бюджет для категории");
        System.out.println("  stats     - Показать статистику");
        System.out.println("  exit      - Выйти из программы");
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();

        if (usersData.containsKey(username)) {
            System.out.println("Пользователь с таким логином уже существует!");
            return;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        User newUser = new User(password);
        usersData.put(username, newUser);

        System.out.println("Пользователь " + username + " успешно зарегистрирован!");
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();

        if (!usersData.containsKey(username)) {
            System.out.println("Пользователь не найден. Пройдите регистрацию (команда register).");
            return;
        }

        User user = usersData.get(username);
        if (!user.password.equals(password)) {
            System.out.println("Неверный пароль!");
            return;
        }

        currentUser = username;
        System.out.println("Вы успешно авторизованы под логином " + currentUser + "!");
    }

    private static void addIncome(Scanner scanner) {
        System.out.print("Введите категорию дохода: ");
        String category = scanner.nextLine().trim();

        System.out.print("Введите сумму дохода: ");
        String amountStr = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число (например, 1000 или 1500.75).");
            return;
        }

        if (amount <= 0) {
            System.out.println("Ошибка: сумма должна быть больше 0.");
            return;
        }

        usersData.get(currentUser).wallet.incomes.add(new Operation(category, amount));
        System.out.println("Доход " + amount + " в категории '" + category + "' добавлен.");
    }

    private static void addExpense(Scanner scanner) {
        System.out.print("Введите категорию расхода: ");
        String category = scanner.nextLine().trim();

        System.out.print("Введите сумму расхода: ");
        String amountStr = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число (например, 500 или 3999.99).");
            return;
        }

        if (amount <= 0) {
            System.out.println("Ошибка: сумма должна быть больше 0.");
            return;
        }

        usersData.get(currentUser).wallet.expenses.add(new Operation(category, amount));
        System.out.println("Расход " + amount + " в категории '" + category + "' добавлен.");
    }

    private static void setBudget(Scanner scanner) {
        System.out.print("Введите название категории: ");
        String category = scanner.nextLine().trim();
        System.out.print("Введите сумму бюджета: ");
        String budgetStr = scanner.nextLine().trim();
        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число (например, 3000 или 4500.50).");
            return;
        }

        if (budget <= 0) {
            System.out.println("Ошибка: сумма должна быть больше 0.");
            return;
        }

        usersData.get(currentUser).wallet.categoriesBudget.put(category, budget);
        System.out.println("Бюджет для категории '" + category + "' установлен: " + budget);
    }

    private static void showStats() {
        Wallet wallet = usersData.get(currentUser).wallet;

        double totalIncome = 0;
        for (Operation inc : wallet.incomes) {
            totalIncome += inc.amount;
        }

        double totalExpense = 0;
        for (Operation exp : wallet.expenses) {
            totalExpense += exp.amount;
        }

        System.out.println("\n--- СТАТИСТИКА ---");
        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpense);

        // Доходы по категориям
        if (!wallet.incomes.isEmpty()) {
            Map<String, Double> incomeByCategory = new HashMap<>();
            for (Operation inc : wallet.incomes) {
                incomeByCategory.put(
                        inc.category,
                        incomeByCategory.getOrDefault(inc.category, 0.0) + inc.amount
                );
            }
            System.out.println("\nДоходы по категориям:");
            for (String cat : incomeByCategory.keySet()) {
                System.out.println("  " + cat + ": " + incomeByCategory.get(cat));
            }
        }

        // Расходы по категориям
        Map<String, Double> expenseByCategory = new HashMap<>();
        for (Operation exp : wallet.expenses) {
            expenseByCategory.put(
                    exp.category,
                    expenseByCategory.getOrDefault(exp.category, 0.0) + exp.amount
            );
        }

        // Проверяем бюджеты
        if (!wallet.categoriesBudget.isEmpty()) {
            System.out.println("\nБюджет по категориям:");
            for (String cat : wallet.categoriesBudget.keySet()) {
                double budget = wallet.categoriesBudget.get(cat);
                double spent = expenseByCategory.getOrDefault(cat, 0.0);
                double remaining = budget - spent;

                System.out.println("  " + cat + ": Бюджет=" + budget +
                        ", Израсходовано=" + spent +
                        ", Остаток=" + remaining);

                if (remaining < 0) {
                    System.out.println("  ВНИМАНИЕ: лимит категории '" + cat + "' превышен!");
                }
            }
        }

        // Если общие расходы > доходов
        if (totalExpense > totalIncome) {
            System.out.println("\nВНИМАНИЕ: общие расходы превышают доходы!");
        }

        System.out.println("--- Конец статистики ---\n");
    }

    private static void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // нет файла - значит, ещё никто не сохранялся
        }

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, User>>(){}.getType();
            HashMap<String, User> loadedData = gson.fromJson(reader, type);
            if (loadedData != null) {
                usersData = loadedData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDataToFile() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            Gson gson = new Gson();
            gson.toJson(usersData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

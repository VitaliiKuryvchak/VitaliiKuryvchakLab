import java.io.*;
import java.util.*;

public class Main {
    static final String FILE_NAME = "phone.txt";
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\n1. Створити новий файл");
            System.out.println("2. Додати запис");
            System.out.println("3. Редагувати запис");
            System.out.println("4. Переглянути всі записи");
            System.out.println("5. Знайти телефон за прізвищем");
            System.out.println("6. Вийти");
            System.out.print("Ваш вибір: ");
            int choice = Integer.parseInt(scan.nextLine());

            switch (choice) {
                case 1 -> createFile();
                case 2 -> addEntry();
                case 3 -> editEntry();
                case 4 -> viewAll();
                case 5 -> searchByName();
                case 6 -> System.exit(0);
                default -> System.out.println("Невірний вибір.");
            }
        }
    }

    static void createFile() throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME));
        while (true) {
            System.out.print("Прізвище Ім'я (або 'stop'): ");
            String name = scan.nextLine();
            if (name.equalsIgnoreCase("stop")) break;
            System.out.print("Телефон: ");
            String phone = scan.nextLine();
            pw.println(name + "," + phone);
        }
        pw.close();
        System.out.println("Файл створено.");
    }

    static void addEntry() throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true));
        System.out.print("Прізвище: ");
        String name = scan.nextLine();
        System.out.print("Телефон: ");
        String phone = scan.nextLine();
        pw.println(name + "," + phone);
        pw.close();
        System.out.println("Запис додано.");
    }

    static void viewAll() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        System.out.println("\nУсі записи:");
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                System.out.println("Прізвище: " + parts[0] + ", Телефон: " + parts[1]);
            }
        }
        br.close();
    }

    static void searchByName() throws IOException {
        System.out.print("Введіть прізвище: ");
        String name = scan.nextLine();
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        boolean found = false;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(name)) {
                System.out.println("Прізвище: " + parts[0] + ", Телефон: " + parts[1]);
                found = true;
            }
        }
        br.close();
        if (!found) System.out.println("Запис не знайдено.");
    }

    static void editEntry() throws IOException {
        System.out.print("Введіть прізвище для редагування: ");
        String name = scan.nextLine();

        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(name)) {
                System.out.print("Новий телефон для " + parts[0] + ": ");
                String newPhone = scan.nextLine();
                lines.add(parts[0] + "," + newPhone);
                found = true;
            } else {
                lines.add(line);
            }
        }
        br.close();

        if (found) {
            PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME));
            for (String l : lines) {
                pw.println(l);
            }
            pw.close();
            System.out.println("Запис(и) оновлено.");
        } else {
            System.out.println("Прізвище не знайдено.");
        }
    }
}

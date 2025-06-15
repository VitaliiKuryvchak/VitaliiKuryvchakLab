//1) У файлі testin.txt записана матриця розміром N * N. Знайти її визначник. Результат
//записати в testout.txt.
//2) Напишіть програму, яка дозволяє знайти в телефонному довіднику (A: \ phone.txt)
//потрібні відомості. Програма повинна запитувати прізвище людини і виводити його
//телефон. Якщо в довіднику є люди з однаковими прізвищами, то програма повинна
//вивести список всіх цих людей.
import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    static final String INPUT_FILE = "testin.txt";
    static final String OUTPUT_FILE = "testout.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\n1. Ввести нову матрицю");
            System.out.println("2. Доповнити файл новою матрицею");
            System.out.println("3. Переглянути матриці у файлі");
            System.out.println("4. Редагувати матрицю");
            System.out.println("5. Обчислити визначники і записати у файл");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> writeNewMatrix();
                case 2 -> appendMatrix();
                case 3 -> viewMatrices();
                case 4 -> editMatrix();
                case 5 -> computeDeterminants();
                case 0 -> System.exit(0);
                default -> System.out.println("Невірний вибір.");
            }
        }
    }

    static void writeNewMatrix() throws IOException {
        PrintWriter pw = new PrintWriter(INPUT_FILE);
        System.out.print("Введіть розмірність матриці N: ");
        int n = sc.nextInt();
        pw.println(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                pw.println(sc.nextDouble());
        pw.close();
        System.out.println("Матрицю записано у файл.");
    }

    static void appendMatrix() throws IOException {
        FileWriter fw = new FileWriter(INPUT_FILE, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        System.out.print("Введіть розмірність нової матриці N: ");
        int n = sc.nextInt();
        pw.println(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                pw.println(sc.nextDouble());
        pw.close();
        System.out.println("Матрицю додано.");
    }

    static void viewMatrices() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
        String line;
        int index = 1;
        while ((line = br.readLine()) != null) {
            int n = Integer.parseInt(line);
            System.out.println("Матриця #" + index++ + " (" + n + "x" + n + "):");
            double[][] matrix = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Double.parseDouble(br.readLine());
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        }
        br.close();
    }

    static void editMatrix() throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(new File(INPUT_FILE).toPath()));
        List<List<String>> matrices = new ArrayList<>();

        for (int i = 0; i < lines.size(); ) {
            int n = Integer.parseInt(lines.get(i));
            List<String> matrix = new ArrayList<>();
            matrix.add(lines.get(i++));
            for (int j = 0; j < n * n; j++) matrix.add(lines.get(i++));
            matrices.add(matrix);
        }

        System.out.print("Яку матрицю редагувати (1-" + matrices.size() + "): ");
        int index = sc.nextInt();
        if (index < 1 || index > matrices.size()) {
            System.out.println("Невірний індекс.");
            return;
        }

        List<String> newMatrix = new ArrayList<>();
        System.out.print("Введіть нову розмірність N: ");
        int n = sc.nextInt();
        newMatrix.add(String.valueOf(n));
        for (int i = 0; i < n * n; i++) {
            newMatrix.add(String.valueOf(sc.nextDouble()));
        }

        matrices.set(index - 1, newMatrix);
        PrintWriter pw = new PrintWriter(INPUT_FILE);
        for (List<String> matrix : matrices)
            for (String l : matrix)
                pw.println(l);
        pw.close();
        System.out.println("Матрицю оновлено.");
    }

    static void computeDeterminants() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
        PrintWriter pw = new PrintWriter(OUTPUT_FILE);
        String line;
        int index = 1;
        while ((line = br.readLine()) != null) {
            int n = Integer.parseInt(line);
            double[][] matrix = new double[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    matrix[i][j] = Double.parseDouble(br.readLine());
            double det = determinant(matrix);
            String result = String.format("Матриця #%d: визначник = %.3f", index++, det);
            pw.println(result);
            System.out.println(result);
        }
        br.close();
        pw.close();
        System.out.println("Результати збережено у testout.txt");
    }


    static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int k = 0; k < n; k++) {
            double[][] minor = new double[n - 1][n - 1];
            for (int i = 1; i < n; i++) {
                int t = 0;
                for (int j = 0; j < n; j++) {
                    if (j == k) continue;
                    minor[i - 1][t++] = matrix[i][j];
                }
            }
            det += matrix[0][k] * Math.pow(-1, k) * determinant(minor);
        }
        return det;
    }
}

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть речення:");
        String input = scanner.nextLine();
        scanner.close();

        String[] words = input.trim().split("\\s+");

        Arrays.sort(words);

        System.out.println(String.join(" ", words));
    }
}

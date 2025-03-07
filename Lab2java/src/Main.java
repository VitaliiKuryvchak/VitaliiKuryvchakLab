import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int size = 10; 
        double[] array = new double[size];
        Random random = new Random();
       
            for (int i = 0; i < size; i++) {
            double number;
            do {
                number = random.nextGaussian() * 0.2 + 0.5;
            } while (number < 0 || number >= 1);
            array[i] = number;
        }
       
        double min = array[0];
        double max = array[0];
        
        for (double num : array) {
            if (num < min) min = num;
            if (num > max) max = num;
        }
        System.out.print("Created numbers ");
        for (double num : array) {
            System.out.printf("%.4f ", num);
        }
        
       System.out.printf("\nMin value: %.4f\n", min);
         System.out.printf("Max value: %.4f\n", max);
    }
}

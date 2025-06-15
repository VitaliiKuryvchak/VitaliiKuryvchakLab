import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Обертання відрізка");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            DrawPanel drawPanel = new DrawPanel();
            frame.add(drawPanel);
            frame.setJMenuBar(drawPanel.createMenuBar(frame));
            frame.setVisible(true);
        });
    }
}

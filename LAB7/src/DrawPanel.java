import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrawPanel extends JPanel implements ActionListener {
    private Timer timer;
    private double angle = 0;
    private double t = 0;

    public DrawPanel() {
        setBackground(Color.WHITE);
        timer = new Timer(20, this); // ~50 fps
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRotatingSegment((Graphics2D) g);
    }

    private void drawRotatingSegment(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();

        // Центр, який рухається (по колу)
        int centerX = (int) (w / 2 + 100 * Math.cos(t));
        int centerY = (int) (h / 2 + 100 * Math.sin(t));

        // Кінець відрізка
        int length = 100;
        int x2 = (int) (centerX + length * Math.cos(angle));
        int y2 = (int) (centerY + length * Math.sin(angle));

        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLUE);
        g.drawLine(centerX, centerY, x2, y2);

        // Малюємо центр
        g.setColor(Color.RED);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angle += 0.05;
        t += 0.01;
        repaint();
    }

    public JMenuBar createMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        // Файл
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem restartItem = new JMenuItem("Перезапустити програму");
        restartItem.addActionListener(e -> {
            angle = 0;
            t = 0;
        });

        JMenuItem exitItem = new JMenuItem("Вихід");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(restartItem);
        fileMenu.add(exitItem);

        // Довідка
        JMenu helpMenu = new JMenu("Довідка");
        JMenuItem helpItem = new JMenuItem("Довідка по Обертання Відрізка");
        helpItem.addActionListener(e -> new HelpWindow());

        JMenuItem aboutItem = new JMenuItem("Про програму");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Програма для демонстрації обертання відрізка\nАвтор: Віталій Куривчак"));

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
}

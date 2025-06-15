import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.io.File;

public class HelpWindow extends JFrame {
    public HelpWindow() {
        setTitle("Довідка");
        setSize(600, 400);

        JTextPane textPane = new JTextPane();
        textPane.setEditorKit(new HTMLEditorKit());
        textPane.setEditable(false);

        try {
            File file = new File("help.html");
            textPane.setPage(file.toURI().toURL());
        } catch (Exception e) {
            textPane.setText("<html><body><h2>Помилка завантаження довідки.</h2></body></html>");
        }

        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane);
        setVisible(true);
    }
}

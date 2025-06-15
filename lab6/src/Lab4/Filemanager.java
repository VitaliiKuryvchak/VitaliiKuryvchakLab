package Lab4;

import Lab4.interfaces.Exportable;

public class Filemanager implements Exportable {

    public void save(String content) {
        System.out.println("Simulating saving content to file:\n" + content);
    }

    public void exportToPDF(String content) {
        System.out.println("Exporting content to PDF...");
    }
}

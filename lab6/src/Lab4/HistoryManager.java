package Lab4;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private final List<String> history = new ArrayList<>();

    public void saveVersion(String content) {
        history.add(content);
    }

    public void printHistory() {
        System.out.println("Edit history:");
        for (String version : history) {
            System.out.println("- " + version);
        }
    }

    public int getVersionCount() {
        return history.size();
    }
}

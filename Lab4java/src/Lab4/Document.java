package Lab4;

import java.time.LocalDateTime;

public class Document {
    private String title;
    private String content;
    private final LocalDateTime createdAt;

    public Document(String title) {
        this.title = title;
        this.content = "";
        this.createdAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

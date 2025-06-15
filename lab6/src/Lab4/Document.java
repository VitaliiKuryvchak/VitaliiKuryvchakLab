package Lab4;

import java.time.LocalDateTime;


class Document implements Comparable<Document> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return title.equals(document.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public int compareTo(Document other) {
        return this.title.compareTo(other.title);
    }
}

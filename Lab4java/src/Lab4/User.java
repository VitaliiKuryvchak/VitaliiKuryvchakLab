package Lab4;

public class User {
    private String username;
    private boolean isEditor;

    public User(String username, boolean isEditor) {
        this.username = username;
        this.isEditor = isEditor;
    }

    public String getUsername() {
        return username;
    }

    public boolean canEdit() {
        return isEditor;
    }
}

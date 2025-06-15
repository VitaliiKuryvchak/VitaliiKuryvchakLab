package Lab4;

class User implements Comparable<User> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(User other) {
        return this.username.compareTo(other.username);
    }
}
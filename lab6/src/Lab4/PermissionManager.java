package Lab4;

public class PermissionManager {
    public boolean hasEditPermission(User user) {
        return user.canEdit();
    }
}

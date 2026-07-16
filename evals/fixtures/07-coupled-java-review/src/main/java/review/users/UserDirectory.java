package review.users;

import review.documents.Document;
import java.util.HashSet;
import java.util.Set;

public final class UserDirectory {
    private final Set<String> users = new HashSet<>();
    public boolean exists(String id) { return users.contains(id); }
    public Document decorate(String id, Document document) { return document; }
}

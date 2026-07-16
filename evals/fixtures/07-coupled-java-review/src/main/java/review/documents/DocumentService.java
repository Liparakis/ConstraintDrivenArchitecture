package review.documents;

import review.users.UserDirectory;

public final class DocumentService {
    private final UserDirectory users;
    public DocumentService(UserDirectory users) { this.users = users; }
    public boolean canEdit(String userId, Document document) { return users.exists(userId) && document != null; }
}

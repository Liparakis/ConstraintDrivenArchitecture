package review.app;

import review.documents.DocumentService;
import review.users.UserDirectory;

public final class Application {
    public static final DocumentService DOCUMENTS = new DocumentService(new UserDirectory());
    private Application() {}
}

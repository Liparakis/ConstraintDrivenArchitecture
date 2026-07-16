package example.app;

import example.auth.SessionPrincipal;
import example.projects.ProjectService;

public final class Application {
    private Application() {}
    public static ProjectService projectService(SessionPrincipal principal) {
        return new ProjectService(principal);
    }
}

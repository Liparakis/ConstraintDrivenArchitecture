package example.projects;

import example.auth.SessionPrincipal;
import java.util.Objects;

public final class ProjectService {
    private final SessionPrincipal principal;
    private final ProjectRepository repository = new example.persistence.JdbcProjectRepository();

    public ProjectService(SessionPrincipal principal) { this.principal = Objects.requireNonNull(principal); }

    public void rename(String id, String name) {
        Project project = repository.find(id).orElseThrow();
        if (!project.ownerUser().equals(principal.userId())) throw new SecurityException("not owner");
        repository.save(new Project(project.id(), project.ownerUser(), name));
    }
}

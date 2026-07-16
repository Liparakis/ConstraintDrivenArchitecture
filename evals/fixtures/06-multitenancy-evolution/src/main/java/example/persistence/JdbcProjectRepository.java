package example.persistence;

import example.projects.Project;
import example.projects.ProjectRepository;
import java.util.Optional;

public final class JdbcProjectRepository implements ProjectRepository {
    public Optional<Project> find(String id) { return Optional.empty(); }
    public void save(Project project) { }
}

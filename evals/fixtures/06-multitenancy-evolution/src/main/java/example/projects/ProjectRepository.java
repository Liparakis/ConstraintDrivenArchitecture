package example.projects;

import java.util.Optional;

public interface ProjectRepository {
    Optional<Project> find(String id);
    void save(Project project);
}

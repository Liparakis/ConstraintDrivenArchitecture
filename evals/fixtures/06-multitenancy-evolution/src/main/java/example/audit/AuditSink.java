package example.audit;

public interface AuditSink {
    void record(String userId, String action, String projectId);
}

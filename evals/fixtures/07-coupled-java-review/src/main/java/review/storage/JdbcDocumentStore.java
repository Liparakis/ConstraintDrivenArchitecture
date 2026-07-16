package review.storage;

import review.documents.Document;

public final class JdbcDocumentStore {
    public Document find(String id) { return new Document(id); }
    public void delete(String id) { /* persistence is reached directly by the controller */ }
}

package review.web;

import review.documents.Document;
import review.storage.JdbcDocumentStore;

public final class DocumentController {
    private final JdbcDocumentStore store = new JdbcDocumentStore();
    public Document load(String id) { return store.find(id); }
    public void delete(String id) { store.delete(id); }
}

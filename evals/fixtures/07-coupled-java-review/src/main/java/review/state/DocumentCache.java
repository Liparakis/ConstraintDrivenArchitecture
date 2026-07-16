package review.state;

import review.documents.Document;
import java.util.HashMap;
import java.util.Map;

public final class DocumentCache {
    public static final Map<String, Document> ITEMS = new HashMap<>();
    private DocumentCache() {}
}

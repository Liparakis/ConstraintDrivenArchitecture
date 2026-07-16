package review.documents;

public final class Document {
    private String title;
    public Document(String title) { this.title = title; }
    public String title() { return title; }
    public void rename(String title) { this.title = title; }
}

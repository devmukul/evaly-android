package bd.com.evaly.evalyshop.models.points;

public class FaqItem {

    private String title;
    private String description;
    private boolean isExpanded;

    public FaqItem() {

    }

    public FaqItem(String title, String description, boolean isExpanded) {
        this.title = title;
        this.description = description;
        this.isExpanded = isExpanded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}

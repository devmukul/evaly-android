package bd.com.evaly.evalyshop.models.search.filter;

public class FilterSubItem {

    private String name;
    private String count;
    private String root;
    private boolean selected;

    public FilterSubItem(String name, String count, String root, boolean selected) {
        this.name = name;
        this.count = count;
        this.root = root;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

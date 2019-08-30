package bd.com.evaly.evalyshop.models;

public class SearchFilterItem {

    private String type, name;
    private boolean selected;
    private int count;


    public SearchFilterItem(){

    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "SearchFilterItem{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", count=" + count +
                '}';
    }
}

package bd.com.evaly.evalyshop.models;

public class SpecificationItem {

    private String nameAttribute;
    private String slugAttribute;
    private boolean filterableAttribute;

    private String optionValue;
    private String optionSlug;

    public SpecificationItem(){

    }

    public String getNameAttribute() {
        return nameAttribute;
    }

    public void setNameAttribute(String nameAttribute) {
        this.nameAttribute = nameAttribute;
    }

    public String getSlugAttribute() {
        return slugAttribute;
    }

    public void setSlugAttribute(String slugAttribute) {
        this.slugAttribute = slugAttribute;
    }

    public boolean isFilterableAttribute() {
        return filterableAttribute;
    }

    public void setFilterableAttribute(boolean filterableAttribute) {
        this.filterableAttribute = filterableAttribute;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getOptionSlug() {
        return optionSlug;
    }

    public void setOptionSlug(String optionSlug) {
        this.optionSlug = optionSlug;
    }
}

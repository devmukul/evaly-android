package bd.com.evaly.evalyshop.models.payment;

public class PaymentMethodModel {

    private String name;
    private String description;
    private String badgeText;
    private int image;
    private boolean isSelected;
    private boolean isEnabled;


    public PaymentMethodModel(String name, String description, String badgeText, int image, boolean isSelected, boolean isEnabled) {
        this.name = name;
        this.description = description;
        this.badgeText = badgeText;
        this.image = image;
        this.isSelected = isSelected;
        this.isEnabled = isEnabled;
    }


    public PaymentMethodModel(String name, String description, int image, boolean isSelected, boolean isEnabled) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.isSelected = isSelected;
        this.isEnabled = isEnabled;
    }

    public PaymentMethodModel(String name, String description, int image, boolean isSelected) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

}

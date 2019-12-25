package bd.com.evaly.evalyshop.models.voucher;

public class VoucherDetails {
    String name,thumnailImage,slug,status;
    int id,value,amount;

    public VoucherDetails() {
    }

    public VoucherDetails(String name, String thumnailImage, String slug, String status, int id, int value, int amount) {
        this.name = name;
        this.thumnailImage = thumnailImage;
        this.slug = slug;
        this.status = status;
        this.id = id;
        this.value = value;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumnailImage() {
        return thumnailImage;
    }

    public void setThumnailImage(String thumnailImage) {
        this.thumnailImage = thumnailImage;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

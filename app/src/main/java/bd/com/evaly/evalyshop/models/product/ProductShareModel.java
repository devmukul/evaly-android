package bd.com.evaly.evalyshop.models.product;

public class ProductShareModel {
    private String p_slug;
    private String p_name;
    private String p_image;
    private String p_price;

    public ProductShareModel(String p_slug, String p_name, String p_image, String p_price) {
        this.p_slug = p_slug;
        this.p_name = p_name;
        this.p_image = p_image;
        this.p_price = p_price;
    }

    public String getP_slug() {
        return p_slug;
    }

    public void setP_slug(String p_slug) {
        this.p_slug = p_slug;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_image() {
        return p_image;
    }

    public void setP_image(String p_image) {
        this.p_image = p_image;
    }

    public String getP_price() {
        return p_price;
    }

    public void setP_price(String p_price) {
        this.p_price = p_price;
    }
}

package bd.com.evaly.evalyshop.util;

public class OrderDetailsProducts {

    String imageUrl,productName,productRate,productQuantity,amount,productSlug,variation;

    public OrderDetailsProducts(){

    }


    public OrderDetailsProducts(String imageUrl, String productName, String productSlug, String productRate, String productQuantity, String amount,String variation) {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.productSlug = productSlug;
        this.productRate = productRate;
        this.productQuantity = productQuantity;
        this.amount = amount;
        this.variation=variation;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductRate() {
        return productRate;
    }

    public void setProductRate(String productRate) {
        this.productRate = productRate;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }
}

package bd.com.evaly.evalyshop.models.shop;

public class AvailableShop {

    private String name,logo,price,slug,contact_number,address,shopJson,maximumPrice,shopSlug, productId;
    private double discountValue;
    private boolean stock;


    public AvailableShop(){

    }

    public AvailableShop(String name, String logo, String price, String slug, String phone, String address, String shopJson,boolean stock,String maximumPrice, String shopSlug, String productId) {
        this.name = name;
        this.logo = logo;
        this.price = price;
        this.slug = slug;
        this.contact_number = phone;
        this.address = address;
        this.shopJson = shopJson;
        this.stock=stock;
        this.maximumPrice=maximumPrice;
        this.shopSlug = shopSlug;
        this.productId = productId;
    }


    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isStock() {
        return stock;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPhone() {
        return contact_number;
    }

    public void setPhone(String phone) {
        this.contact_number = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopJson() {
        return shopJson;
    }

    public void setShopJson(String shopJson) {
        this.shopJson = shopJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPrice() {
        return price;
    }

    public int getPriceInt() {
        try {
            return (int) Double.parseDouble(price);
        } catch (Exception e){
            return 0;
        }
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean getStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public String getMaximumPrice() {
        return maximumPrice;
    }

    public int getMaximumPriceInt() {
        try {
            return (int) Double.parseDouble(maximumPrice);
        } catch (Exception e){
            return 0;
        }
    }

    public void setMaximumPrice(String maximumPrice) {
        this.maximumPrice = maximumPrice;
    }
}

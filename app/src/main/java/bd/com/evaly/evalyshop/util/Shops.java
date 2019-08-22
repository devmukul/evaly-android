package bd.com.evaly.evalyshop.util;

public class Shops {

    String shopID,shopName,logo[],address,slug,area,ownerContactNumber,ownerUsername,latitude,longitude;
    boolean ownerVerified,shopApproved;
    int sellingItemsCount;

    public Shops(String shopID, String shopName, String[] logo, String address, String slug, String area, String ownerContactNumber, String ownerUsername, String latitude, String longitude, boolean ownerVerified, boolean shopApproved, int sellingItemsCount) {
        this.shopID = shopID;
        this.shopName = shopName;
        this.logo = logo;
        this.address = address;
        this.slug = slug;
        this.area = area;
        this.ownerContactNumber = ownerContactNumber;
        this.ownerUsername = ownerUsername;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerVerified = ownerVerified;
        this.shopApproved = shopApproved;
        this.sellingItemsCount = sellingItemsCount;
    }

    public Shops() {
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String[] getLogo() {
        return logo;
    }

    public void setLogo(String[] logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOwnerContactNumber() {
        return ownerContactNumber;
    }

    public void setOwnerContactNumber(String ownerContactNumber) {
        this.ownerContactNumber = ownerContactNumber;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isOwnerVerified() {
        return ownerVerified;
    }

    public void setOwnerVerified(boolean ownerVerified) {
        this.ownerVerified = ownerVerified;
    }

    public boolean isShopApproved() {
        return shopApproved;
    }

    public void setShopApproved(boolean shopApproved) {
        this.shopApproved = shopApproved;
    }

    public int getSellingItemsCount() {
        return sellingItemsCount;
    }

    public void setSellingItemsCount(int sellingItemsCount) {
        this.sellingItemsCount = sellingItemsCount;
    }
}

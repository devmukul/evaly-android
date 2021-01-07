package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

public class AttributesItem {

    @SerializedName("attribute_data")
    private AttributeData attributeData;

    @SerializedName("attribute_values")
    private String attributeValues;

    @SerializedName("attribute_slug")
    private String attributeSlug;

    @SerializedName("attribute_name")
    private String attributeName;


    public AttributeData getAttributeData() {
        return attributeData;
    }

    public void setAttributeData(AttributeData attributeData) {
        this.attributeData = attributeData;
    }

    public void setAttributeValues(String attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getAttributeSlug() {
        return attributeSlug;
    }

    public void setAttributeSlug(String attributeSlug) {
        this.attributeSlug = attributeSlug;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String toString() {
        return
                "AttributesItem{" +
                        "attribute_values = '" + attributeValues + '\'' +
                        ",attribute_slug = '" + attributeSlug + '\'' +
                        ",attribute_name = '" + attributeName + '\'' +
                        "}";
    }
}
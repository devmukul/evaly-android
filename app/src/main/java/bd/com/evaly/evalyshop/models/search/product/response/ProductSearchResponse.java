package bd.com.evaly.evalyshop.models.search.product.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductSearchResponse{

	@SerializedName("products")
	private List<ProductsItem> products;

	@SerializedName("facets")
	private Facets facets;

	public void setProducts(List<ProductsItem> products){
		this.products = products;
	}

	public List<ProductsItem> getProducts(){
		return products;
	}

	public void setFacets(Facets facets){
		this.facets = facets;
	}

	public Facets getFacets(){
		return facets;
	}

	@Override
 	public String toString(){
		return 
			"ProductSearchResponse{" + 
			"products = '" + products + '\'' + 
			",facets = '" + facets + '\'' + 
			"}";
		}
}
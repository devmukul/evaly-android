package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class AddressResponse{

	@SerializedName("area")
	private String area;

	@SerializedName("address")
	private String address;

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("city")
	private String city;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("id")
	private int id;

	@SerializedName("region")
	private String region;

	@SerializedName("status")
	private String status;

	public void setArea(String area){
		this.area = area;
	}

	public String getArea(){
		return area;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setRegion(String region){
		this.region = region;
	}

	public String getRegion(){
		return region;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"AddressResponse{" + 
			"area = '" + area + '\'' + 
			",address = '" + address + '\'' + 
			",full_name = '" + fullName + '\'' + 
			",city = '" + city + '\'' + 
			",phone_number = '" + phoneNumber + '\'' + 
			",id = '" + id + '\'' + 
			",region = '" + region + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}
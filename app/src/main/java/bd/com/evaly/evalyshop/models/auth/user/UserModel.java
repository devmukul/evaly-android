package bd.com.evaly.evalyshop.models.auth.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel{

	@SerializedName("time_lines")
	private List<Object> timeLines;

	@SerializedName("country")
	private String country;

	@SerializedName("email_verification")
	private boolean emailVerification;

	@SerializedName("origin")
	private String origin;

	@SerializedName("mobile_no")
	private String mobileNo;

	@SerializedName("phone_verification")
	private boolean phoneVerification;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("user_type")
	private String userType;

	@SerializedName("permissions")
	private List<Object> permissions;

	@SerializedName("contact_and_basic_info")
	private ContactAndBasicInfo contactAndBasicInfo;

	@SerializedName("profile_pic_url")
	private String profilePicUrl;

	@SerializedName("profile_pic_url_sm")
	private String profilePicUrlSm;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("group")
	private Group group;

	@SerializedName("updatedAt")
	private String updatedAt;

	@SerializedName("is_superuser")
	private boolean isSuperuser;

	@SerializedName("is_active")
	private boolean isActive;

	@SerializedName("is_staff")
	private boolean isStaff;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("created_by")
	private String createdBy;

	@SerializedName("country_code")
	private String countryCode;

	@SerializedName("device_info")
	private List<Object> deviceInfo;

	@SerializedName("_id")
	private String id;

	@SerializedName("status")
	private String status;

	@SerializedName("username")
	private String username;

	public void setTimeLines(List<Object> timeLines){
		this.timeLines = timeLines;
	}

	public List<Object> getTimeLines(){
		return timeLines;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setEmailVerification(boolean emailVerification){
		this.emailVerification = emailVerification;
	}

	public boolean isEmailVerification(){
		return emailVerification;
	}

	public void setOrigin(String origin){
		this.origin = origin;
	}

	public String getOrigin(){
		return origin;
	}

	public void setMobileNo(String mobileNo){
		this.mobileNo = mobileNo;
	}

	public String getMobileNo(){
		return mobileNo;
	}

	public void setPhoneVerification(boolean phoneVerification){
		this.phoneVerification = phoneVerification;
	}

	public boolean isPhoneVerification(){
		return phoneVerification;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setUserType(String userType){
		this.userType = userType;
	}

	public String getUserType(){
		return userType;
	}

	public void setPermissions(List<Object> permissions){
		this.permissions = permissions;
	}

	public List<Object> getPermissions(){
		return permissions;
	}

	public void setContactAndBasicInfo(ContactAndBasicInfo contactAndBasicInfo){
		this.contactAndBasicInfo = contactAndBasicInfo;
	}

	public ContactAndBasicInfo getContactAndBasicInfo(){
		return contactAndBasicInfo;
	}

	public void setProfilePicUrl(String profilePicUrl){
		this.profilePicUrl = profilePicUrl;
	}

	public String getProfilePicUrl(){
		return profilePicUrl;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public Group getGroup(){
		return group;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setIsSuperuser(boolean isSuperuser){
		this.isSuperuser = isSuperuser;
	}

	public boolean isIsSuperuser(){
		return isSuperuser;
	}

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setIsStaff(boolean isStaff){
		this.isStaff = isStaff;
	}

	public boolean isIsStaff(){
		return isStaff;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setCreatedBy(String createdBy){
		this.createdBy = createdBy;
	}

	public String getCreatedBy(){
		return createdBy;
	}

	public void setCountryCode(String countryCode){
		this.countryCode = countryCode;
	}

	public String getCountryCode(){
		return countryCode;
	}

	public void setDeviceInfo(List<Object> deviceInfo){
		this.deviceInfo = deviceInfo;
	}

	public List<Object> getDeviceInfo(){
		return deviceInfo;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"UserModel{" + 
			"time_lines = '" + timeLines + '\'' + 
			",country = '" + country + '\'' + 
			",email_verification = '" + emailVerification + '\'' + 
			",origin = '" + origin + '\'' + 
			",mobile_no = '" + mobileNo + '\'' + 
			",phone_verification = '" + phoneVerification + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",createdAt = '" + createdAt + '\'' + 
			",user_type = '" + userType + '\'' + 
			",permissions = '" + permissions + '\'' + 
			",contact_and_basic_info = '" + contactAndBasicInfo + '\'' + 
			",profile_pic_url = '" + profilePicUrl + '\'' + 
			",first_name = '" + firstName + '\'' + 
			",group = '" + group + '\'' + 
			",updatedAt = '" + updatedAt + '\'' + 
			",is_superuser = '" + isSuperuser + '\'' + 
			",is_active = '" + isActive + '\'' + 
			",is_staff = '" + isStaff + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",created_by = '" + createdBy + '\'' + 
			",country_code = '" + countryCode + '\'' + 
			",device_info = '" + deviceInfo + '\'' + 
			",_id = '" + id + '\'' + 
			",status = '" + status + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}

	public String getProfilePicUrlSm() {
		return profilePicUrlSm;
	}

	public void setProfilePicUrlSm(String profilePicUrlSm) {
		this.profilePicUrlSm = profilePicUrlSm;
	}
}
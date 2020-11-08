package bd.com.evaly.evalyshop.models.appointment;

import com.google.gson.annotations.SerializedName;

public class AppointmentRequest{

	@SerializedName("date")
	private String date;

	@SerializedName("category")
	private String category;

	@SerializedName("day")
	private String day;

	@SerializedName("time_slot")
	private String timeSlot;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setDay(String day){
		this.day = day;
	}

	public String getDay(){
		return day;
	}

	public void setTimeSlot(String timeSlot){
		this.timeSlot = timeSlot;
	}

	public String getTimeSlot(){
		return timeSlot;
	}

	@Override
 	public String toString(){
		return 
			"AppointmentRequest{" + 
			"date = '" + date + '\'' + 
			",category = '" + category + '\'' + 
			",day = '" + day + '\'' + 
			",time_slot = '" + timeSlot + '\'' + 
			"}";
		}
}
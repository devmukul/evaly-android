package bd.com.evaly.evalyshop.models.appointment;

import com.google.gson.annotations.SerializedName;

public class AppointmentTimeSlotResponse {

	@SerializedName("available")
	private boolean available;

	@SerializedName("time_slot")
	private String timeSlot;

	public void setAvailable(boolean available){
		this.available = available;
	}

	public boolean isAvailable(){
		return available;
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
			"AppointmentTimeSlotItem{" + 
			"available = '" + available + '\'' + 
			",time_slot = '" + timeSlot + '\'' + 
			"}";
		}
}
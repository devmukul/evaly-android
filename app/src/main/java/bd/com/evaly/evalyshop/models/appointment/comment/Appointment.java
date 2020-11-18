package bd.com.evaly.evalyshop.models.appointment.comment;

import com.google.gson.annotations.SerializedName;

public class Appointment{

	@SerializedName("appointment_id")
	private String appointmentId;

	public void setAppointmentId(String appointmentId){
		this.appointmentId = appointmentId;
	}

	public String getAppointmentId(){
		return appointmentId;
	}

	@Override
 	public String toString(){
		return 
			"Appointment{" + 
			"appointment_id = '" + appointmentId + '\'' + 
			"}";
		}
}
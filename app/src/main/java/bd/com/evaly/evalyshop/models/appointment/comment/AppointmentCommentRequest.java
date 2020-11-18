package bd.com.evaly.evalyshop.models.appointment.comment;

import com.google.gson.annotations.SerializedName;

public class AppointmentCommentRequest{

	@SerializedName("appointment")
	private String appointment;

	@SerializedName("comment")
	private String comment;

	public void setAppointment(String appointment){
		this.appointment = appointment;
	}

	public String getAppointment(){
		return appointment;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	@Override
 	public String toString(){
		return 
			"AppointmentCommentRequest{" + 
			"appointment = '" + appointment + '\'' + 
			",comment = '" + comment + '\'' + 
			"}";
		}
}
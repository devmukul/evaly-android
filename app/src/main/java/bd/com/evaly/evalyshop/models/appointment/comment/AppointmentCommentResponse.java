package bd.com.evaly.evalyshop.models.appointment.comment;

import com.google.gson.annotations.SerializedName;

public class AppointmentCommentResponse{

	@SerializedName("commented_by")
	private CommentedBy commentedBy;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("comment")
	private String comment;

	@SerializedName("appointment")
	private Appointment appointment;

	@SerializedName("id")
	private int id;

	public void setCommentedBy(CommentedBy commentedBy){
		this.commentedBy = commentedBy;
	}

	public CommentedBy getCommentedBy(){
		return commentedBy;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	public void setAppointment(Appointment appointment){
		this.appointment = appointment;
	}

	public Appointment getAppointment(){
		return appointment;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"AppointmentCommentResponse{" + 
			"commented_by = '" + commentedBy + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",comment = '" + comment + '\'' + 
			",appointment = '" + appointment + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}
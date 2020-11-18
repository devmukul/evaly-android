package bd.com.evaly.evalyshop.models.appointment.list;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppointmentResponse implements Serializable {

    @SerializedName("date")
    private String date;

    @SerializedName("appointment_id")
    private String appointmentId;

    @SerializedName("counter")
    private String counter;

    @SerializedName("category")
    private Category category;

    @SerializedName("day")
    private String day;

    @SerializedName("created_by")
    private CreatedBy createdBy;

    @SerializedName("status")
    private String status;

    @SerializedName("time_slot")
    private String timeSlot;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getCounter() {
        return counter;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    @Override
    public String toString() {
        return
                "AppointmentResponse{" +
                        "date = '" + date + '\'' +
                        ",appointment_id = '" + appointmentId + '\'' +
                        ",counter = '" + counter + '\'' +
                        ",category = '" + category + '\'' +
                        ",day = '" + day + '\'' +
                        ",created_by = '" + createdBy + '\'' +
                        ",status = '" + status + '\'' +
                        ",time_slot = '" + timeSlot + '\'' +
                        "}";
    }
}
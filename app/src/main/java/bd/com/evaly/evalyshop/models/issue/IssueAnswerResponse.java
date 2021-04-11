package bd.com.evaly.evalyshop.models.issue;

import com.google.gson.annotations.SerializedName;

public class IssueAnswerResponse{

	@SerializedName("is_active")
	private boolean isActive;

	@SerializedName("answer")
	private int answer;

	@SerializedName("has_input_field")
	private boolean hasInputField;

	@SerializedName("id")
	private int id;

	@SerializedName("text")
	private String text;

	@SerializedName("category")
	private int category;

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setAnswer(int answer){
		this.answer = answer;
	}

	public int getAnswer(){
		return answer;
	}

	public void setHasInputField(boolean hasInputField){
		this.hasInputField = hasInputField;
	}

	public boolean isHasInputField(){
		return hasInputField;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setCategory(int category){
		this.category = category;
	}

	public int getCategory(){
		return category;
	}

	@Override
 	public String toString(){
		return 
			"IssueAnswerResponse{" + 
			"is_active = '" + isActive + '\'' + 
			",answer = '" + answer + '\'' + 
			",has_input_field = '" + hasInputField + '\'' + 
			",id = '" + id + '\'' + 
			",text = '" + text + '\'' + 
			",category = '" + category + '\'' + 
			"}";
		}
}
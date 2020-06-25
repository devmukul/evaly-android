package bd.com.evaly.evalyshop.models;

import com.google.gson.annotations.SerializedName;

public class MetaInfo{

	@SerializedName("next")
	private String next;

	@SerializedName("previous")
	private String previous;

	@SerializedName("count")
	private int count;

	@SerializedName("page_size")
	private int pageSize;

	public void setNext(String next){
		this.next = next;
	}

	public String getNext(){
		return next;
	}

	public void setPrevious(String previous){
		this.previous = previous;
	}

	public String getPrevious(){
		return previous;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}

	public int getPageSize(){
		return pageSize;
	}

	@Override
 	public String toString(){
		return 
			"MetaInfo{" + 
			"next = '" + next + '\'' + 
			",previous = '" + previous + '\'' + 
			",count = '" + count + '\'' + 
			",page_size = '" + pageSize + '\'' + 
			"}";
		}
}
package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

public class BlockHistoryItem{

	@SerializedName("blocked_by")
	private String blockedBy;

	@SerializedName("block_reason")
	private String blockReason;

	@SerializedName("time")
	private String time;

	public void setBlockedBy(String blockedBy){
		this.blockedBy = blockedBy;
	}

	public String getBlockedBy(){
		return blockedBy;
	}

	public void setBlockReason(String blockReason){
		this.blockReason = blockReason;
	}

	public String getBlockReason(){
		return blockReason;
	}

	public void setTime(String time){
		this.time = time;
	}

	public String getTime(){
		return time;
	}

	@Override
 	public String toString(){
		return 
			"BlockHistoryItem{" + 
			"blocked_by = '" + blockedBy + '\'' + 
			",block_reason = '" + blockReason + '\'' + 
			",time = '" + time + '\'' + 
			"}";
		}
}
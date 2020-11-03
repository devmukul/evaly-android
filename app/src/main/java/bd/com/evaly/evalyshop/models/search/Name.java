package bd.com.evaly.evalyshop.models.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Name{

	@SerializedName("matchLevel")
	private String matchLevel;

	@SerializedName("fullyHighlighted")
	private boolean fullyHighlighted;

	@SerializedName("value")
	private String value;

	@SerializedName("matchedWords")
	private List<String> matchedWords;

	public void setMatchLevel(String matchLevel){
		this.matchLevel = matchLevel;
	}

	public String getMatchLevel(){
		return matchLevel;
	}

	public void setFullyHighlighted(boolean fullyHighlighted){
		this.fullyHighlighted = fullyHighlighted;
	}

	public boolean isFullyHighlighted(){
		return fullyHighlighted;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	public void setMatchedWords(List<String> matchedWords){
		this.matchedWords = matchedWords;
	}

	public List<String> getMatchedWords(){
		return matchedWords;
	}

	@Override
 	public String toString(){
		return 
			"Name{" + 
			"matchLevel = '" + matchLevel + '\'' + 
			",fullyHighlighted = '" + fullyHighlighted + '\'' + 
			",value = '" + value + '\'' + 
			",matchedWords = '" + matchedWords + '\'' + 
			"}";
		}
}
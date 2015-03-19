package cs454.seo.Indexer;

public class PageIndex {

	String uUId;
	
	int wordCount;
	
	double totalFactor;
	
	int titleRank;

	public String getuUId() {
		return uUId;
	}

	public void setuUId(String uUId) {
		this.uUId = uUId;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public double getTotalFactor() {
		return totalFactor;
	}

	public void setTotalFactor(double totalFactor) {
		this.totalFactor = totalFactor;
	}

	public int getTitleRank() {
		return titleRank;
	}

	public void setTitleRank(int titleRank) {
		this.titleRank = titleRank;
	}
	
	
	
}
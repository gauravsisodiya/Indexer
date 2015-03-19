package cs454.seo.Indexer;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class LinkData {
	
	private String id;
	private String path;
	private List<String> goingOut;
	private List<String> incoming;
	private int pointedBy;
	private double rank;
	private double newRank;
	private String url;
	private double finalRank1;
	private double finalRank2;
	JSONObject json;
	
	public LinkData() {
		super();
		goingOut = new ArrayList<String>();
		json = new JSONObject();
		incoming = new ArrayList<String>();
	}
	public LinkData(String id, String path, List<String> goingOut,
			int pointedBy, double rank, String url) {
		super();
		this.id = id;
		this.path = path;
		this.goingOut = goingOut;
		this.pointedBy = pointedBy;
		this.rank = rank;
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<String> getGoingOut() {
		return goingOut;
	}
	public void setGoingOut(List<String> goingOut) {
		this.goingOut = goingOut;
	}
	public int getPointedBy() {
		return pointedBy;
	}
	public void setPointedBy(int pointedBy) {
		this.pointedBy = pointedBy;
	}
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public double getNewRank() {
		return newRank;
	}
	public void setNewRank(double newRank) {
		this.newRank = newRank;
	}
	
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
	}
	public void copyRank(){
		this.rank = this.newRank;
	}
	
	@SuppressWarnings("unchecked")
	public void createJSON(){
		this.json.put("id", this.id);
		this.json.put("URL", this.url);
		this.json.put("Link Analysis Rank",this.finalRank1);
		this.json.put("Our Ranking", this.finalRank2);
	}
	public double getFinalRank1() {
		return finalRank1;
	}
	public void setFinalRank1() {
		this.finalRank1 = this.rank;
	}
	public double getFinalRank2() {
		return finalRank2;
	}
	public void setFinalRank2() {
		this.finalRank2 = this.rank;
	}
	public List<String> getIncoming() {
		return incoming;
	}
	public void setIncoming(List<String> incoming) {
		this.incoming = incoming;
	}
}

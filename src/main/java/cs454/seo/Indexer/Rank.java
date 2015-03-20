package cs454.seo.Indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Rank {

	private int numOfLinks;
	
	private List<LinkData> allLinks = new ArrayList<LinkData>();
	private HashMap<String, Integer> incomingCount = new HashMap<String, Integer>();
	private HashMap<String, Integer> outgoingCount = new HashMap<String, Integer>();
	private HashMap<String, LinkData> records = new HashMap<String, LinkData>();
	private JSONArray mainArr = new JSONArray();
	private JSONObject jsonObj = new JSONObject();

	public void saveArray() throws IOException {
		FileWriter file = new FileWriter(".\\ranking.json", false);
	
		file.write(jsonObj.toJSONString());
		file.write("\r\n");
		file.flush();
		file.close();
	}

	public void start(String filePath) throws FileNotFoundException,
			IOException, ParseException {
		File jsonFile = new File(filePath);
	
		JSONParser jsonParser = new JSONParser();
	
		JSONArray jsonArr = (JSONArray) jsonParser.parse(new FileReader(
				jsonFile));
	
		numOfLinks = jsonArr.size();

		JSONObject jsonObject;
		LinkData link;
		String path;
		String temp[];
		String url;
		JSONObject links;
		List<String> outGoing;
		String linkHolder;

	
		for (Object obj : jsonArr) {
			jsonObject = (JSONObject) obj;
			link = new LinkData();
			path = (String) jsonObject.get("path");
	
			temp = path.split("\\\\");
			link.setId(temp[temp.length - 2]);
	
			url = (String) jsonObject.get("URL");
			links = (JSONObject) jsonObject.get("links");
			outGoing = new ArrayList<String>();
	
			for (Object l : links.keySet()) {
				linkHolder = (String) links.get(l);
				if (!linkHolder.equals(""))
					outGoing.add(linkHolder);

			}
	
			link.setPath(path);
			link.setUrl(url);
			link.setGoingOut(outGoing);
	
			allLinks.add(link);
			records.put(link.getUrl(), link);
		}

	
		int tempStore;

		for (LinkData single : allLinks) {

			for (String eachUrl : single.getGoingOut()) {
				if (incomingCount.containsKey(eachUrl)) {
					tempStore = incomingCount.get(eachUrl);
					incomingCount.put(eachUrl, ++tempStore);
				} else
					incomingCount.put(eachUrl, 1);
				
				single.getIncoming().add(eachUrl);
			}
		}

		for (String single : incomingCount.keySet()) {
			if (records.containsKey(single)) {
				records.get(single).setPointedBy(incomingCount.get(single));
			}
		}


		beginMyRanking();

	}
	

	public void beginMyRanking() throws IOException {
		
		double defaultRank = 1.0 / numOfLinks;
		

		for (LinkData link : allLinks) {
			link.setRank(defaultRank);
			link.setNewRank(defaultRank);
		}

		double rank;
		double tempRank;
		LinkData holder;
		for (int i = 0; i < 10; i++) {
	
			for (String url : records.keySet()) {
				holder = records.get(url);
				rank = 0;
				for (String goingOut : holder.getGoingOut()) {
					if (records.containsKey(goingOut)) {
						tempRank = records.get(goingOut).getRank();
						if (incomingCount.containsKey(goingOut))
							if (incomingCount.get(goingOut) > 0)
								tempRank = tempRank
										/ incomingCount.get(goingOut);
						rank = rank + tempRank;
					} else {
						rank = rank + defaultRank;
					}
				}
				if (rank == 0.0)
					rank = defaultRank;

				holder.setNewRank(rank);
				holder.setFinalRank1();
			}
			for (LinkData link : allLinks) {
				link.copyRank();
			}
		}
		
		for (LinkData link : allLinks) {
			link.getFinalRank1();
		}
		
		beginOrignalRanking();

	}
	
	@SuppressWarnings("unchecked")
	public void beginOrignalRanking() throws IOException {
		
		double defaultRank = 1.0 / numOfLinks;
		

		for (LinkData link : allLinks) {
			link.setRank(defaultRank);
			link.setNewRank(defaultRank);
			outgoingCount.put(link.getUrl(), link.getGoingOut().size());
		}

		double rank;
		double tempRank;
		LinkData holder;
		for (int i = 0; i < 10; i++) {
		
			for (String url : records.keySet()) {
				holder = records.get(url);
				rank = 0;
				for (String incoming : holder.getIncoming()) {
					if (records.containsKey(incoming)) {
						tempRank = records.get(incoming).getRank();
						if (outgoingCount.containsKey(incoming))
							if (outgoingCount.get(incoming) > 0)
								tempRank = tempRank
										/ outgoingCount.get(incoming);
						rank = rank + tempRank;
					} else {
						rank = rank + defaultRank;
					}
				}
				if (rank == 0.0)
					rank = defaultRank;

				holder.setNewRank(rank);
				holder.setFinalRank2();
			}
			for (LinkData link : allLinks) {
				link.copyRank();
			}
		}

		for (LinkData link : allLinks) {
			link.setFinalRank2();
			link.round();
			link.createJSON();
			jsonObj.put(link.getId(), link.getJson());
			mainArr.add(link.getJson());
		}
		saveArray();

	}
	
	//code changes made by shreyas
	public void LastRanking() throws IOException {
		System.out.println("Number of Links" + numOfLinks);
		double defaultRank = 1.0 / numOfLinks;
		System.out.println("Default Rank " + defaultRank);

		for (LinkData link : allLinks) {
			link.setRank(defaultRank);
			link.setNewRank(defaultRank);
		}

		double rank;
		double tempRank;
		LinkData holder;
		for (int i = 0; i < 10; i++) {
			// System.out.println("Iteration number : "+i);
			for (String url : records.keySet()) {
				holder = records.get(url);
				rank = 0;
				for (String goingOut : holder.getGoingOut()) {
					if (records.containsKey(goingOut)) {
						tempRank = records.get(goingOut).getRank();
						if (incomingCount.containsKey(goingOut))
							if (incomingCount.get(goingOut) > 0)
								tempRank = tempRank
										/ incomingCount.get(goingOut);
						rank = rank + tempRank;
					} else {
						rank = rank + defaultRank;
					}
				}
				if (rank == 0.0)
					rank = defaultRank;

				// System.out.println("Rank : "+rank);

				holder.setNewRank(rank);
				holder.setFinalRank1();
			}
			for (LinkData link : allLinks) {
				link.copyRank();
			}
		}
		
		for (LinkData link : allLinks) {
			link.getFinalRank1();
		}
		
		beginOrignalRanking();

	}

	

}

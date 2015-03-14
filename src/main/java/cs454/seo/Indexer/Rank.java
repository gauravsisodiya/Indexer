package cs454.seo.Indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Rank {
	
	private int numOfLinks;
	//private HashMap<String, List<String>> urlMap = new HashMap<String, List<String>>();
	private List<LinkData> allLinks = new ArrayList<LinkData>();
	private HashMap<String,Integer> incomingCount = new HashMap<String,Integer>();
	private HashMap<String, LinkData> records = new HashMap<String, LinkData>();
	private JSONArray mainArr = new JSONArray();
	
	public void saveArray() throws IOException{
		FileWriter file = new FileWriter(".\\ranking.json",false);
		file.write(mainArr.toJSONString());
		file.write("\r\n");
		file.flush();
		file.close();
	}
	
	public void start(String filePath) throws FileNotFoundException, IOException, ParseException{
		File jsonFile = new File(filePath);
        //System.out.println(jsonFile);
        JSONParser jsonParser = new JSONParser();
		//JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFile));
		JSONArray jsonArr = (JSONArray) jsonParser.parse(new FileReader(jsonFile));
		//System.out.println(jsonArr);
		
		numOfLinks = jsonArr.size();
		
		
		
		JSONObject jsonObject;
		LinkData link;
		String path;
		String temp[];
		String url;
		JSONObject links;
		List<String> outGoing;
		String linkHolder;
		
		//Object obj = jsonArr.get(0);
		
		
		for(Object obj : jsonArr){
			jsonObject = (JSONObject) obj;
			link = new LinkData();
			path = (String)jsonObject.get("path");
			//System.out.println(path);
			temp = path.split("\\\\");
			link.setId(temp[temp.length-2]);
			//System.out.println(temp[temp.length-2]);
			url = (String) jsonObject.get("URL");
			links = (JSONObject) jsonObject.get("links");
			outGoing = new ArrayList<String>();
			//System.out.println(links);
			for(Object l : links.keySet()){
				linkHolder = (String) links.get(l);
				if(!linkHolder.equals(""))
					outGoing.add(linkHolder);
				
			}
			//System.out.println();
			link.setPath(path);
			link.setUrl(url);
			link.setGoingOut(outGoing);
			//System.out.println(jsonObject);
			allLinks.add(link);
			records.put(link.getUrl(), link);
		}
		
		//start calculating incoming links here
		Iterator itr = allLinks.iterator();
		int tempStore;
		
		for(LinkData single : allLinks){
			
			for(String eachUrl : single.getGoingOut()){
				if(incomingCount.containsKey(eachUrl))
				{
					tempStore = incomingCount.get(eachUrl);
					incomingCount.put(eachUrl, ++tempStore);
				}
				else
					incomingCount.put(eachUrl, 1);
			}
		}
		
		for(String single : incomingCount.keySet()){
			if(records.containsKey(single)){
				records.get(single).setPointedBy(incomingCount.get(single));
			}
		}
		
		
		
		/*for(LinkData tempLink : allLinks)
			System.out.println(tempLink.getPointedBy());*/
		//System.out.println(allLinks);
		//System.out.println(numOfLinks);
		beginRanking();
		
	}
	
	public void beginRanking() throws IOException{
		System.out.println("Number of Links"+numOfLinks);
		double defaultRank = 1.0 / numOfLinks;
		System.out.println("Default Rank "+defaultRank);
		
		for(LinkData link : allLinks){
			link.setRank(defaultRank);
			link.setNewRank(defaultRank);
		}
		
		double rank;
		double tempRank;
		LinkData holder;
		for(int i = 0; i<10; i++){
			//System.out.println("Iteration number : "+i);
		for(String url : records.keySet()){
			holder = records.get(url);
			rank = 0;
			for(String goingOut : holder.getGoingOut()){
				if(records.containsKey(goingOut)){
					tempRank = records.get(goingOut).getRank();
					if(incomingCount.containsKey(goingOut))
						if(incomingCount.get(goingOut)>0)
							tempRank = tempRank / incomingCount.get(goingOut);
					rank = rank + tempRank;
				}
				else
				{
					rank = rank + defaultRank;
				}
			}
			if(rank == 0.0)
				rank = defaultRank;
			
			//System.out.println("Rank : "+rank);
			
			holder.setNewRank(rank);
		}
		for(LinkData link : allLinks){
			link.copyRank();
		}
		}
		
		for(LinkData link : allLinks){
			System.out.println(link.getUrl()+" -> "+link.getRank());
			link.createJSON();
			mainArr.add(link.getJson());
		}
		saveArray();
		
	}

}

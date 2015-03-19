package cs454.seo.Indexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Index {

	
	public Map<String, List<PageIndex>> wordCount = new HashMap<String, List<PageIndex>>();
	public static List<String> stopWords = new ArrayList<String>();
	
	public void controlIndex(String filePath) {
		JSONParser parser = new JSONParser();
		System.out.println("Indexing Started...");
		
		try {

			JSONArray jsonArray = (JSONArray) parser
					.parse(new FileReader(
							filePath));

			JSONObject jsonObject = new JSONObject();
			JSONObject metaObj ;
			for (Object obj : jsonArray) {
				jsonObject = (JSONObject) obj;
				String path = jsonObject.get("path").toString()
						.replaceAll("\\\\", "/");
				metaObj = (JSONObject) jsonObject.get("MetaData");
				String title ="";
				if(metaObj.get("title")!= null)
				{
					title = metaObj.get("title").toString();
				}
				String UUId = path.substring(path.lastIndexOf("/") + 1,
						path.lastIndexOf("."));
				performIndex(path, UUId,title);
				performMetadataIndex(jsonObject.get("MetaData"),UUId);
			}
			
			System.out.println("Indexing Completed...Now writing to Indexer.json File");
			//printWordCount();
			fileWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void performMetadataIndex(Object object, String UUId) {
		
		JSONObject jsonObject =  (JSONObject) object;
		
		if(jsonObject.get("title")!= null)
		{
			String title = jsonObject.get("title").toString();
			StringTokenizer stringTokenizer = new StringTokenizer(title," -.");
			while(stringTokenizer.hasMoreElements())
			{
				String element = stringTokenizer.nextToken();
				addIndex2Map(element, UUId);
			}
		}
		
		if(jsonObject.get("Description")!= null)
		{
			String title = jsonObject.get("Description").toString();
			StringTokenizer stringTokenizer = new StringTokenizer(title," .");
			while(stringTokenizer.hasMoreElements())
			{
				String element = stringTokenizer.nextToken();
				addIndex2Map(element, UUId);
			}
		}
		
		if(jsonObject.get("Author")!= null)
		{
			String title = jsonObject.get("Author").toString();
			StringTokenizer stringTokenizer = new StringTokenizer(title," ,.");
			while(stringTokenizer.hasMoreElements())
			{
				String element = stringTokenizer.nextToken();
				addIndex2Map(element, UUId);
			}
		}
	}

	private void performIndex(String path, String UUId, String title) {
		
		try {
			
			Parser parser = new AutoDetectParser();
			BodyContentHandler contentHandler = new BodyContentHandler(10*1024*1024);
			Metadata metadata = new Metadata();
			
			FileInputStream inputStream = new FileInputStream(path);
			ParseContext context = new ParseContext();
			parser.parse(inputStream, contentHandler, metadata, context);
			StringTokenizer stringTokenizer = new StringTokenizer(
					contentHandler.toString().replaceAll("\\s+", " ")," .,-#\\n//");
			
			while (stringTokenizer.hasMoreTokens()) {
				String element = stringTokenizer.nextToken();
				element = cleanElement(element);

				if (!stopWords.equals((element)) && checkElements(element) == false && element.length()>2 && isNumeric(element) == false) {
					addIndex2Map(element, UUId);
					
					addTitle2HashMap(element,title,UUId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void addIndex2Map(String element,String UUId)
	{
		List<PageIndex> pageWordList = new ArrayList<PageIndex>();
		PageIndex pageIndex = new PageIndex();
		
		if (wordCount.get(element) == null) {
			pageIndex.setuUId(UUId);
			pageIndex.setWordCount(1);
			pageWordList.add(pageIndex);
			wordCount.put(element, pageWordList);
		
		} 
		else {
			pageWordList = wordCount.get(element);
			boolean flag = true;
			for(PageIndex p : pageWordList)
			{
				if(p.getuUId().equalsIgnoreCase(UUId))
				{
					int count =  p.getWordCount();
					p.setWordCount(++count);
					flag = false;
				}
			}
			if(flag)
			{
				pageIndex = new PageIndex();
				pageIndex.setuUId(UUId);
				pageIndex.setWordCount(1);
				pageWordList.add(pageIndex);
			}
			wordCount.put(element, pageWordList);
		}
	}

	private void addTitle2HashMap(String element, String title, String UUId) {
		
		List<PageIndex> pageIndexList = wordCount.get(element);
		for(PageIndex p : pageIndexList)
		{
			if(p.getuUId().equals(UUId))
			{
				if(title.toLowerCase().contains(element))
				{
					p.setTitleRank(1);
				}
				else
				{
					p.setTitleRank(0);
				}
			}
		}
	}

	public List<String> getStopWords(String path) {
		try {
			
			FileReader inputFile = new FileReader(path);
			@SuppressWarnings("resource")
			BufferedReader bufferReader = new BufferedReader(inputFile);

			String line;

			while ((line = bufferReader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				while (tokenizer.hasMoreTokens()) {
					stopWords.add(tokenizer.nextToken().trim());
				}
			}
		} catch (Exception e) {
			System.out.println("Error while reading file  by line:"
					+ e.getMessage());
		}
		return stopWords;

	}

	public void printWordCount() {
		for (String word : wordCount.keySet()) {
			System.out.println("Word: " + word);
			for(PageIndex p:wordCount.get(word))
			{
				System.out.println("UUId:: "+p.getuUId());
				System.out.println("Count:: "+p.getWordCount());
				System.out.println("TitleRank:: "+p.getTitleRank());
			}
		}

	}
	

	public String cleanElement(String element) {

		element = element.toLowerCase();
		element = element.trim();

		if (element.endsWith(".")) {
			element = element.substring(0, element.lastIndexOf("."));
		} else if (element.endsWith(" .")) {
			element = element.substring(0, element.lastIndexOf(" ."));
		} else if (element.endsWith(" ,")) {
			element = element.substring(0, element.lastIndexOf(" ,"));
		} else if (element.endsWith(",")) {
			element = element.substring(0, element.lastIndexOf(","));
		}

		return element;
	}
	
	@SuppressWarnings("unchecked")
	public void fileWriter() throws IOException
	{
		JSONArray jsonArray;
		List<PageIndex> pageIndex;
		JSONObject jsonObj;
		JSONObject jsonPage = new JSONObject();
		
		for(String word : wordCount.keySet())
		{
			jsonArray = new JSONArray();
			pageIndex =  wordCount.get(word);
			double totalSize = pageIndex.size();
			double termFreq = 0.0;
			for(PageIndex page : pageIndex)
			{
				jsonObj = new JSONObject();
				jsonObj.put("UUId", page.getuUId().toString());
				jsonObj.put("Count", Integer.toString(page.getWordCount()));
				termFreq = page.getWordCount() / totalSize;
				termFreq =Double.parseDouble(new DecimalFormat("##.##").format(termFreq));
				jsonObj.put("TermFrequency", Double.toString(termFreq));
				jsonObj.put("TitleRank", Integer.toString(page.getTitleRank()));
				jsonArray.add(jsonObj);
			}
			jsonPage.put(word, jsonArray);
			
		}
		
		FileWriter file = new FileWriter("./indexer.json",false);
        try {
            file.write(jsonPage.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            file.flush();
            file.close();
        }
		
	}
	
	public boolean checkElements(String element)
	{
		if(element.equals("#") || element.equals("&") || element.equals("+") || element.equals("-") || element.equals("") || element.equals("|") || element.equals(".") || element.equals("\\\\"))
			return true;
		
		return false;
	}
	
	public boolean isNumeric(String element)
	{
		String regex = "[0-9]+";
		if(element.matches(regex))
			return true;
		
		return false;
	}

}

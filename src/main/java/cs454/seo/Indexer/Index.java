package cs454.seo.Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.ls.LSInput;

public class Index {

	public Map<String, Map<String, Integer>> wordCount = new HashMap<String, Map<String, Integer>>();
	public static List<String> stopWords = new ArrayList<String>();

	public void controlIndex(String filePath) {
		JSONParser parser = new JSONParser();
		System.out.println(stopWords);
		
		try {

			JSONArray jsonArray = (JSONArray) parser
					.parse(new FileReader(
							filePath));

			JSONObject jsonObject = new JSONObject();
			for (Object obj : jsonArray) {
				jsonObject = (JSONObject) obj;
				String path = jsonObject.get("path").toString()
						.replaceAll("\\\\", "/");
				String UUId = path.substring(path.lastIndexOf("/") + 1,
						path.lastIndexOf("."));
				performIndex(path, UUId);
			}
			if(stopWords.contains("+"))
				System.out.println("correct");
			//printWordCount();
			fileWriter();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void performIndex(String path, String UUId) {
		try {
			Parser parser = new AutoDetectParser();
			BodyContentHandler contentHandler = new BodyContentHandler(10*1024*1024);
			Metadata metadata = new Metadata();

			FileInputStream inputStream = new FileInputStream(path);
			ParseContext context = new ParseContext();
			parser.parse(inputStream, contentHandler, metadata, context);
			StringTokenizer stringTokenizer = new StringTokenizer(
					contentHandler.toString()," .,-#");
			while (stringTokenizer.hasMoreTokens()) {
				String element = stringTokenizer.nextToken();
				element = cleanElement(element);

				if (!stopWords.equals((element)) && checkElements(element) == false) {
					Map<String, Integer> pageWordCount = new HashMap<String, Integer>();
					if (wordCount.get(element) == null) {
						pageWordCount.put(UUId, 1);
						wordCount.put(element, pageWordCount);
					} else {
						pageWordCount = wordCount.get(element);
						if (pageWordCount.get(UUId) == null) {
							pageWordCount.put(UUId, 1);
						} else {
							int count = pageWordCount.get(UUId);
							pageWordCount.put(UUId, ++count);
						}

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<String> getStopWords(String path) {
		try {
			System.out.println(path);
			FileReader inputFile = new FileReader(path);
			BufferedReader bufferReader = new BufferedReader(inputFile);

			String line;

			while ((line = bufferReader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				while (tokenizer.hasMoreTokens()) {
					stopWords.add(tokenizer.nextToken().trim());
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error while reading file  by line:"
					+ e.getMessage());
		}
		return stopWords;

	}

	public void printWordCount() {
		for (String word : wordCount.keySet()) {
			System.out.println("Word: " + word);
			System.out.println(wordCount.get(word));
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
	
	public void fileWriter() throws IOException
	{
		JSONArray jsonArray = new JSONArray();
		Map<String, Integer> pageMap;
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonPage = new JSONObject();
		
		for(String word : wordCount.keySet())
		{
			jsonObj = new JSONObject();
			jsonObj.put("Word", word.toString());
			pageMap =  wordCount.get(word);
			jsonPage = new JSONObject();
			for(String page : pageMap.keySet())
			{
				jsonPage.put(page, pageMap.get(page).toString());
			}
			jsonArray.add(jsonObj);
			jsonArray.add(jsonPage);
			
		}
		
		FileWriter file = new FileWriter("./indexer.json");
        try {
            file.write(jsonArray.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            
 
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            file.flush();
            file.close();
        }
		
		//System.out.println(jsonArray.toString());
	}

	
	public boolean checkElements(String element)
	{
		if(element.equals("#") || element.equals("&") || element.equals("+") || element.equals("-") || element.equals("") || element.equals("|") || element.equals(".") || element.equals("\\\\"))
			return true;
		
		return false;
	}

}

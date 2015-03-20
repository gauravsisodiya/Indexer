package cs454.seo.Indexer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, IOException, ParseException
    {
    	//String path = "C:\\Users\\Gaurav\\CS454_Web_Search_Engine\\extractor-app\\Control.json";
    	//String stopPath = "C:\\Users\\Gaurav\\CS454_Web_Search_Engine\\Indexer\\stopwords.txt";
    	String path = args[1];
    	String stopPath = args[3];
    	
        Index index = new Index();
        index.getStopWords(stopPath);
        System.out.println("Indexing Started...");

        index.controlIndex(path);
        System.out
		.println("Indexing Completed...Result written to Indexer.json File");
        
        Rank ranking = new Rank();
        System.out.println("Ranking Started...");

        ranking.start(path);
        System.out
		.println("Ranking Completed...Result written to Ranking.json File");
    }
}

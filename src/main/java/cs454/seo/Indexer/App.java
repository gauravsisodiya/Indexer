package cs454.seo.Indexer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, IOException, ParseException
    {
    	String path = "C:\\Users\\Gaurav\\CS454_Web_Search_Engine\\extractor-app\\Control.json";
    	String stopPath = "C:\\Users\\Gaurav\\CS454_Web_Search_Engine\\Indexer\\stopwords.txt";
    	//String path = args[1];
    	//String stopPath = args[3];
    	
        Index index = new Index();
        index.getStopWords(stopPath);
        index.controlIndex(path);
        //Rank ranking = new Rank();
        //ranking.start(path);
    }
}

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class Scrapper {
	
	public static void main(String[] args) throws Exception{

		if(args.length < 1)
		{
			System.out.println("Please enter the Hackden.com URL");
			return;
		}
		
		ArrayList<String> commonWords =  new ArrayList<String>();
		String token1 = "";

		//Input File Containing the list of Stop words
		Scanner inFile1 = new Scanner(new File("CommonWords.txt")).useDelimiter("\n");

		// Adding all the stop words to a ArrayList
	    while (inFile1.hasNext()) {
	      token1 = inFile1.next();
	      if( !token1.isEmpty() && (token1.length() > 1) )
	    	  commonWords.add(token1.trim());
	    }
	    inFile1.close();
	    
	    
	    
	    //String baseurl = "http://www.hacksden.com/showthread.php/757-Music-(contains-adult-language!)";
	    String baseurl = args[0];
	    String newurl = baseurl;
	    int pageNo = 2;
	    
	    
	    
		ArrayList<String> textHolder = new ArrayList<String>();
		boolean endParsing = false;
		
		
	    
		FileWriter fw = new FileWriter("file.txt");

		int postID = 1;
		
		
		try{	
		while(!endParsing)
		{
			
			// Connect to the Url using the JSoup Library
		Document doc = Jsoup.connect(newurl).timeout(3000).get();
		Elements text = doc.select("div[class=content]");
		
		// I am Adding the full text to an arraylist so that i can check at each iteration if te page has already been read
		// in Hackden Website if an unknown page num is loaded we just load the last page Using this i have devised a methd to stop the parsing
		String fullText = text.text();
		if(!textHolder.contains(fullText))
			textHolder.add(fullText);
		else 
			{
				endParsing = true;
				System.out.println("Stopped at Page no "+ pageNo);
			}
		
		if(!endParsing)
		{
		for(Element e : text){
		String desc = e.text();
		String[] words = desc.split("\\s+");
		for (int i = 0; i < words.length; i++) {
		    words[i] = words[i].replaceAll("[^\\w]", ""); // replace all non word characters like smilies etc with empty
		}		
		
		boolean flag = false; 
	    for (int i = 0; i < words.length; i++) {
	    	flag = false;
	    	for(String s : commonWords){ 	// Compare and add to the File only if it doesnt exist in the outfile
	    		if(s.equalsIgnoreCase(words[i].trim()))
	    		{
	    			flag = true;
	    			break;
	    		}
	    	}
    		if (flag == false)	
	    		fw.write(postID + "," + words[i]+"\r\n");
	    	}
	    	postID++;
		}
		newurl = baseurl +"/page"+pageNo;
    	pageNo++;
    	System.out.println("The new URL is : "+ newurl);
		}
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
			fw.close();
			System.out.println("Error Parsing the Thread , Total Number of Pages parsed : "+ pageNo);
		}
		fw.close();
		
		//return 0;
	}
}

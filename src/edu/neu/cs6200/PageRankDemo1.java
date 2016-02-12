package edu.neu.cs6200;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class PageRankDemo1 {
	public final static double TELEPORTATION_FACTOR = 0.85;
	public static double SHANNON_ENTROPY = 0;

	public static void main(String[] args) {
	
		LinkedHashMap<String,PageInfo> pages = new LinkedHashMap<String,PageInfo> ();
		ArrayList<String> sinkpages = new ArrayList<String>();
		LinkedHashMap<String,Integer> outlinks = new LinkedHashMap<String,Integer>();
		
		readInputfileAndCalculateOutlinks("6nodes.txt",pages,outlinks); // load the input file in data structure
		
		getsinkNodes(sinkpages,pages,outlinks); //get all the pages with no outlinks
		
		calculatePageRank(pages,sinkpages,outlinks); //calculate the pageRank values and write to file
	}
	
	private static void getsinkNodes(ArrayList<String> sinkpages, LinkedHashMap<String, PageInfo> pages, LinkedHashMap<String, Integer> outlinks) {
		for(Map.Entry<String,PageInfo> entry : pages.entrySet() )
		{
			String pgName = entry.getKey();
			if (outlinks.get(pgName) == 0)
			{
				sinkpages.add(pgName);
			}
		}
	}

	private static void calculatePageRank(LinkedHashMap<String, PageInfo> pages, ArrayList<String> sinkpages, LinkedHashMap<String, Integer> outlinks) {
		double sinkPR;
		int count = 1;
		for(Map.Entry<String, PageInfo> entry : pages.entrySet())
		{
			PageInfo p = entry.getValue();
			p.setPageRankvalue((double)1/pages.size());
			
		}
		
		BufferedWriter output = null;
        try {
		 File file = new File("6nodeResult.txt");
         output = new BufferedWriter(new FileWriter(file));
         String text = "";
		
		while(count <= 100)
		{
			sinkPR = 0;
			for (String p : sinkpages)
				sinkPR+= pages.get(p).getPageRankvalue();
			
			for (Map.Entry<String, PageInfo> entry : pages.entrySet())
			{
				PageInfo p = entry.getValue();
				double newPageRankvalue;
				newPageRankvalue = (double) (1 - TELEPORTATION_FACTOR)/pages.size();
				newPageRankvalue += (double) (TELEPORTATION_FACTOR * sinkPR/pages.size());
				
				for (String q : p.getInlinkPages())
				{
					newPageRankvalue += (TELEPORTATION_FACTOR * pages.get(q).getPageRankvalue()/noOfOutlinks(q, outlinks));
				}
				
				p.setPageRankvalue(newPageRankvalue);
			}
			
			if(count == 1 || count == 10 || count == 100 )
			{  
	            text = "Page Rank values for iteration "+count+"\n";
	            output.write(text);
	            
	            for(Map.Entry<String, PageInfo> entry : pages.entrySet())
	    		{
	    			String pgName = entry.getKey();
	    			PageInfo pginfo = entry.getValue();
	    		    text = pgName + ":";

	    			text += " Page Rank value: "+pginfo.getPageRankvalue() + "\n";
	    			
	    			 output.write(text);
	    			 
	    		}
			}
			
			count ++;
		}
        }
        catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null )
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
		System.out.println("Number of iterations :"+ count);
		
	}

	private static int noOfOutlinks(String q, LinkedHashMap<String, Integer> outlinks) {
		
		return outlinks.get(q);
	}

	private static void readInputfileAndCalculateOutlinks(String string,LinkedHashMap<String, PageInfo> pages, LinkedHashMap<String, Integer> outlinks) {
		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(string));

			while ((sCurrentLine = br.readLine()) != null) {
				
				String pgElement[] = sCurrentLine.split(" ");
				
				PageInfo pinf = new PageInfo();
				
				LinkedHashSet<String> inlinks = new LinkedHashSet<String>();
				if (outlinks.get(pgElement[0]) == null)
					outlinks.put(pgElement[0], 0);
				for(int j=1; j< pgElement.length ; j++)
				{
					int prev_size = inlinks.size();
					inlinks.add(pgElement[j]);
					int new_size = inlinks.size();
					if (outlinks.get(pgElement[j]) == null)
					outlinks.put(pgElement[j], 1);
					
					else
					{
						if(prev_size != new_size)
						outlinks.put(pgElement[j],outlinks.get(pgElement[j]) + 1);
					}
					
				}
				pinf.setInlinkPages(inlinks);
				pages.put(pgElement[0], pinf);
			}
				
		}
		 catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		} //end finally
		
	} //end of readInput file

}

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

public class PageRankDemo2 {
	public final static double TELEPORTATION_FACTOR = 0.85;
	public static double SHANNON_ENTROPY = 0;

	public static void main(String[] args) {
	
		LinkedHashMap<String,PageInfo> pages = new LinkedHashMap<String,PageInfo> ();
		ArrayList<String> sinkpages = new ArrayList<String>();
		LinkedHashMap<String,Integer> outlinks = new LinkedHashMap<String,Integer>();
		
		readInputfileAndCalculateOutlinks("nodes.txt",pages,outlinks); // load the input file in data structure
		
		getsinkNodes(sinkpages,pages,outlinks); //get all the pages with no outlinks
		
		calculatePageRank(pages,sinkpages,outlinks); //calculate the pageRank values
		
		System.out.println("Writing pages with their page rank values to list_of_pages_with_rank.txt....");
		writeToFile(pages,outlinks,"list_of_pages_with_rank.txt"); // writing pages by page rank values for all nodes	
		
		LinkedHashMap<String,PageInfo> page_sorted_by_pgrank = (LinkedHashMap<String, PageInfo>) sortBasedOnPageRankValues(pages,sinkpages,outlinks);
		LinkedHashMap<String,PageInfo> page_sorted_by_inlink = (LinkedHashMap<String, PageInfo>) sortBasedOnInLinkValues(pages,sinkpages,outlinks);
		
		// writing pages sorted by page rank for the first 50 nodes
		System.out.println("Writing pages sorted by page rank to list_of_pages_sorted_by_rank.txt....");
		writeToFile(page_sorted_by_pgrank,outlinks,"list_of_pages_sorted_by_rank.txt"); 
		
		// writing pages sorted by inlink count for the first 50 nodes
		System.out.println("Writing pages sorted by page rank to list_of_pages_sorted_by_inlink_count.txt....");
		writeToFile(page_sorted_by_inlink,outlinks,"list_of_pages_sorted_by_inlink_count.txt"); 
		
		/* Writing the list of proportions as mentioned in the problem set
		 * Writing proportion of pages with no inlinks
		 * Writing  proportion of pages with no outlinks
		 * Writing proportion of pages whose page rank value is less than their initial/uniform
		*/
		System.out.println("Writing proportion of pages to proportions.txt.....");
		writeProportionsToFile(pages,sinkpages,"proportions.txt");
		
	}

	private static void writeProportionsToFile(LinkedHashMap<String, PageInfo> pages, ArrayList<String> sinkpages,
			String string) {
		BufferedWriter output = null;
        try {
            File file = new File(string);
            output = new BufferedWriter(new FileWriter(file));
            String text = "";
            
            text+="Proportion of pages with no inlinks: "+ calculateProportionNoInlinks(pages)+ "\n";
            text+="Proportion of pages with no outlinks: "+ (double) sinkpages.size()/pages.size()+"\n";
            text+="Proportion of pages whose page rank values is less than their initial: "+ calculateProportion(pages);
            
            output.write(text);
            
        } catch ( IOException e ) {
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
		
	}

	private static double calculateProportion(LinkedHashMap<String, PageInfo> pages) {
		double noOfPages = 0;
		for(Map.Entry<String, PageInfo> entry : pages.entrySet())
		{
			PageInfo pinf = entry.getValue();
			if(pinf.getPageRankvalue() < (double) 1 / pages.size())
				noOfPages++;
		}
		return (double) noOfPages/pages.size();
	}

	private static double calculateProportionNoInlinks(LinkedHashMap<String, PageInfo> pages) {
		
		double noOfPages = 0;
		for(Map.Entry<String, PageInfo> entry : pages.entrySet())
		{
			PageInfo pinf = entry.getValue();
			if(pinf.getInlinkPages().size() == 0)
				noOfPages++;
		}
		return (double) noOfPages/pages.size();
	}

	private static Map<String, PageInfo> sortBasedOnInLinkValues(LinkedHashMap<String, PageInfo> pages,
			ArrayList<String> sinkpages, LinkedHashMap<String, Integer> outlinks) {
		List<Map.Entry<String, PageInfo>> entries =
				  new ArrayList<Map.Entry<String, PageInfo>>(pages.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String, PageInfo>>() {
				  public int compare(Map.Entry<String, PageInfo> a, Map.Entry<String, PageInfo> b){
					  
					  if (a.getValue().getInlinkPages().size() < (b.getValue().getInlinkPages().size()))
							  return 1;
					  else if (a.getValue().getInlinkPages().size() > (b.getValue().getInlinkPages().size()))
						  return -1;
					  else
						  return 0;
				  }
				});
				Map<String, PageInfo> sortedMap = new LinkedHashMap<String, PageInfo>();
				for (Map.Entry<String, PageInfo> entry : entries) {
				  sortedMap.put(entry.getKey(), entry.getValue());
				  
				}
				return sortedMap;
	}

	private static Map<String, PageInfo> sortBasedOnPageRankValues(LinkedHashMap<String, PageInfo> pages, ArrayList<String> sinkpages,
			LinkedHashMap<String, Integer> outlinks) {
		
		List<Map.Entry<String, PageInfo>> entries =
				  new ArrayList<Map.Entry<String, PageInfo>>(pages.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String, PageInfo>>() {
					
				  public int compare(Map.Entry<String, PageInfo> a, Map.Entry<String, PageInfo> b){
					  if (a.getValue().getPageRankvalue() < (b.getValue().getPageRankvalue()))
							  return 1;
					  else if (a.getValue().getPageRankvalue() > (b.getValue().getPageRankvalue()))
						  return -1;
					  else
						  return 0;
				  }
				});
				Map<String, PageInfo> sortedMap = new LinkedHashMap<String, PageInfo>();
				for (Map.Entry<String, PageInfo> entry : entries) {
				  sortedMap.put(entry.getKey(), entry.getValue());
				  
				}
				return sortedMap;
	}
	

	private static void writeToFile(LinkedHashMap<String, PageInfo> pages, LinkedHashMap<String, Integer> outlinks, String string) {
		BufferedWriter output = null;
        try {
            File file = new File(string);
            output = new BufferedWriter(new FileWriter(file));
            int i = 1;
            
            for(Map.Entry<String, PageInfo> entry : pages.entrySet())
    		{
            	if(i > 50 && !string.equals("list_of_pages_with_rank.txt"))
    				break;
    			String pgName = entry.getKey();
    			PageInfo pginfo = entry.getValue();
    			String text = pgName + ":";
    			
    			text += " Page Rank value: "+pginfo.getPageRankvalue();
    			//if(string.equals("list_of_pages_sorted_by_inlink_count.txt"))
    			text +=  "| Inlink Count:" +pginfo.getInlinkPages().size();
    			
    			text += "| Outlink Count: " +outlinks.get(pgName)+ "\n"; 
    			//text += "\n";
    			 output.write(text);
    			 i++;
    		}
        
        } catch ( IOException e ) {
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
		ArrayList<Double> perplexity = new ArrayList<Double>();
		
		BufferedWriter output = null;
        try {
		 File file = new File("perplexity.txt");
         output = new BufferedWriter(new FileWriter(file));
         String text = "";
		
		while(!converged(pages,perplexity,count,output))
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


	private static boolean converged(LinkedHashMap<String, PageInfo> pages, ArrayList<Double> perplexity, int count, BufferedWriter output) throws IOException {
		SHANNON_ENTROPY = 0;
		
            for(Map.Entry<String, PageInfo> entry : pages.entrySet())
            {
            	PageInfo pinf = entry.getValue();
            	SHANNON_ENTROPY+= (pinf.getPageRankvalue() * Math.log(pinf.getPageRankvalue()));
            }
		
            SHANNON_ENTROPY = SHANNON_ENTROPY * -1;
            perplexity.add(Math.pow(2, SHANNON_ENTROPY));
		
            System.out.println("Perplexity value for iteration "+count +" : "+ Math.pow(2, SHANNON_ENTROPY));
		
            output.write("Perplexity value for iteration "+count +" : "+ Math.pow(2, SHANNON_ENTROPY)+"\n");
        

		if(perplexity.size() == 4 && isEqual(perplexity))
			return true;
		else
		{
			if(perplexity.size() == 4) 
				perplexity.remove(0);
			return false;
		}	
	}

	private static boolean isEqual(ArrayList<Double> perplexity) {
		for(int i = 1 ; i< perplexity.size() ; i++)
		{
			double diff = perplexity.get(i-1) - perplexity.get(i);
			if(diff >= 1.0)
				return false;	
		}
		return true;
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

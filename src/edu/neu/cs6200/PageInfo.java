package edu.neu.cs6200;

import java.util.LinkedHashSet;

public class PageInfo {
	LinkedHashSet<String> inlinkPages;
	double pageRankvalue;
	
	public PageInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkedHashSet<String> getInlinkPages() {
		return inlinkPages;
	}

	public void setInlinkPages(LinkedHashSet<String> inlinkPages) {
		this.inlinkPages = inlinkPages;
	}

	public double getPageRankvalue() {
		return pageRankvalue;
	}

	public void setPageRankvalue(double pageRankvalue) {
		this.pageRankvalue = pageRankvalue;
	}

}

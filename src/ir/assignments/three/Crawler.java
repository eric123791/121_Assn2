package ir.assignments.three;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {

	//helper for 2
	public static List<String> urlList = new ArrayList<String>();
	static int numUrl = 0;

	//helper for 3
	public static Map<String, Integer> subdomainList  = new HashMap<String, Integer>();

	//helper for 4
	static int longestPageLength = 0;
	static String longestPageUrl;

	//helper for 5
	static List<String> wordsList = new ArrayList<String>();



	private static final Pattern FILTERS = Pattern.compile(".*\\.(bmp|gif|jpg|png|css|js|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz)$");
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of image extensions.
		if (FILTERS.matcher(href).matches()) 
			return false;
		if(!href.contains(".ics.uci.edu"))
			return false;

		return findTrap(href);
	}

	private boolean findTrap (String href)
	{
		if (href.contains("?"))
			return false;
		else if (href.contains("calendar.ics.uci.edu")) 
			return false;
		else if (href.contains("http://archive.ics.uci.edu/ml/datasets.html")) 
			return false;

		return true;	
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {

		//getting page date
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		logger.debug("Docid: {}", docid);
		logger.info("URL: {}", url);
		logger.debug("Domain: '{}'", domain);
		logger.debug("Sub-domain: '{}'", subDomain);
		logger.debug("Path: '{}'", path);
		logger.debug("Parent page: {}", parentUrl);
		logger.debug("Anchor text: {}", anchor);

		//add to url if not found
		if(!urlList.contains(url))
		{
			urlList.add(url);
			++numUrl;			
		}
		
		//add to subdomainList 1)not found add new 2) increment the count
		if(!subdomainList.containsKey(subDomain))
			subdomainList.put(subDomain, 1);
		else
		{
			subdomainList.put(subDomain, subdomainList.get(subDomain)+1);
		}

		//getting data of the page
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			if(longestPageLength < text.length())
			{
				longestPageLength = text.length();
				longestPageUrl = url;
			}	
			
			try {
				wordsList.addAll(Utilities.tokenizeString(text));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("add words to words problem");
				e.printStackTrace();
			}
			
			
			logger.debug("Text length: {}", text.length());
			logger.debug("Html length: {}", html.length());
			logger.debug("Number of outgoing links: {}", links.size());
			
		}

		//no idea
		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			logger.debug("Response headers:");
			for (Header header : responseHeaders) {
				logger.debug("\t{}: {}", header.getName(), header.getValue());
			}
		}

		logger.debug("=============");
	}

	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
	public static Collection<String> crawl(String seedURL) {
		// TODO implement me
		return null;
	}
}

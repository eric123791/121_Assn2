
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.*;

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
	public static Map<String, Integer> subdomainList  = new TreeMap<String, Integer>();
	static int numSubDomain = 0;

	//helper for 4
	public static int longestPageLength = 0;
	public static String longestPageUrl;

	//helper for 5
	public static Map<String, Integer> wordList = new TreeMap<String, Integer>();


	private static final Pattern FILTERS = Pattern.compile(".*\\.(css|js|bmp|gif|jpe?g|ico" + "|png|tiff?|mid|mp2|mp3|mp4"
			    + "|wav|avi|mov|mpeg|ram|m4v|mkv|ogg|ogv|pdf" 
			    + "|ps|eps|tex|ppt|pptx|doc|docx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1" 
			    + "|thmx|mso|arff|rtf|jar|csv"
			    + "|rm|smil|wmv|swf|wma|zip|rar|gz)$");
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of image extensions.
		if (FILTERS.matcher(href).matches())
			return false;
		if(!href.contains(".ics.uci.edu"))
			return false;

		if (href.contains("?") && href.contains("="))
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
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("url.txt", true)))) {
   		 out.println(url);
		}catch (IOException e) {}
		}

		//add to subdomainList 1)not found add new 2) increment the count
		if(!subdomainList.containsKey(subDomain+ "." +domain))
		{
			subdomainList.put(subDomain+ "." +domain, 1);
			++numSubDomain;
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("subdomain.txt", true)))) {
				out.println(subDomain+ "." +domain);
				}catch (IOException e) {}
		}
		else
		{
			subdomainList.put(subDomain, subdomainList.get(subDomain+ "." +domain)+1);
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

			//need to modiefied//////////////////////////////////////////////////////////////
			processWord(text);


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

	
	public static void printAllWords() throws Exception
	{
		PrintWriter writer = new PrintWriter("CommonWords.txt");
		for(Map.Entry<String,Integer> entry : wordList.entrySet())
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			writer.println(key+" : "+value);
		}
		writer.close();
	}
	
	public static void print500Words() throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		int i = 0;
		for(Map.Entry<String,Integer> entry : wordList.entrySet())
		{
			if(i>=500)
			{
				out.close();
				return;
			}
			String key = entry.getKey();
			Integer value = entry.getValue();
			out.println(key+" : "+value);
			++i;
		}
		out.close();
	}

	public static void printUrl() throws Exception
	{
		PrintWriter writer = new PrintWriter("URL.txt");
			int size  = urlList.size();
			for(int i = 0; i < size; ++i)
			{
				writer.write(urlList.get(i));
			}
		writer.close();
	}

	public static void printSubDomain() throws Exception
	{
		PrintWriter writer = new PrintWriter("Subdomains.txt");
		for(Map.Entry<String,Integer> entry : subdomainList.entrySet())
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			writer.println(key+" : "+value);
		}
		writer.close();
	}
	
	private static void processWord(String words)
	{
		try{
			ArrayList<String> temp = Utilities.tokenizeString(words);
		
			int size = temp.size();
		
			for(int i = 0; i < size; ++i)
			{
				Integer n = wordList.get(temp.get(i));
				n = (n == null) ? 1: ++n;
				wordList.put(temp.get(i), n);
			}
		}catch( Exception e){
		System.out.println("PROCESS WORDS ERROR");
			return;
		}
		
	}

}


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.*;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {

	//helper for 2
	public static HashSet<String> urlList = new HashSet<String>();
	static int numUrl = 0;

	//helper for 3
	public static Map<String, Integer> subDomainList  = new HashMap<String, Integer>();
	static int numSubDomain = 0;

	//helper for 4
	public static int longestPageLength = 0;
	public static String longestPageUrl;

	//helper for 5
	public static Map<String, Integer> wordList = new HashMap<String, Integer>();
	private static HashSet<String> stopWordsList = stopWords();


	private static final Pattern FILTERS = Pattern.compile(".*\\.(css|js|bmp|gif|jpe?g|ico" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|mkv|ogg|ogv|pdf"
			+ "|ps|eps|tex|ppt|pptx|doc|docx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1"
			+ "|thmx|mso|arff|rtf|jar|csv"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz)$");


	public static HashSet<String> tokenize(File input) throws Exception {
		// TODO Write body!
		String path = input.getName();
		Scanner  sc = new Scanner (new FileReader(path));
		HashSet<String> result = new HashSet<String> ();

		while(sc.hasNext())
		{
			String line = sc.next();
			line = line.toLowerCase().replaceAll("[^a-z1-9']+", "");
			result.add(line);
		}
		sc.close();
		return result;
	}
	private static HashSet<String> stopWords()
	{
		try{
			File file = new File("stopwordlist.txt");
			HashSet <String> result =  tokenize(file);
			return result;
		}catch(Exception e){
			System.out.println("STOPWORDS ERROR");
		}
		return null;
	}
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of image extensions.
		if (FILTERS.matcher(href).matches())
			return false;
		if(!href.contains(".ics.uci.edu"))
			return false;
		if(href.contains("?"))
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
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String subDomain = page.getWebURL().getSubDomain();

		//logger.info("URL: {}", url);


		//backup plan putting url and subdomain into repective txt file
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("url.txt", true)))) {
			out.println(url);
		}catch (IOException e) {}

		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("subdomain.txt", true)))) {
			out.println(subDomain+ "." +domain);
		}catch (IOException e)
		{}

		//getting data of the page
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();


			if(longestPageLength < text.length())
			{
				longestPageLength = text.length();
				longestPageUrl = url;
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("longesturl.txt", true)))) {
					out.println(longestPageUrl + " " + longestPageLength);
				}catch (IOException e)
				{}
			}

			//backup plan
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("listofwords.txt", true)))) {
				out.println(text);
			}catch (IOException e)
			{}
		}

	}

	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 *
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 * @throws Exception 
	 */
	public static Collection<String> crawl(String seedURL) throws Exception {
		String crawlStorageFolder = "crawlStorageFolder";

		int numberOfCrawlers = 7;
		String userAgent = "UCI Inf141-CS121 crawler 49399981";
		int timeOut = 1000*60*15;

		CrawlConfig config = new CrawlConfig();
		config.setUserAgentString(userAgent);

		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(550);
		config.setResumableCrawling(true);
		config.setMaxPagesToFetch(100);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);


		controller.addSeed(seedURL);

		long start = System.currentTimeMillis()/1000;

		controller.start(Crawler.class, numberOfCrawlers);

		long end = System.currentTimeMillis()/1000;
		long totalTime = end - start;
		
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(totalTime);
		String time = timeOfDay.toString();

		System.out.println("finished");
		processEverything();

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		//Answer 1
		out.println("1. It took " + time + " to finish.");
		out.close();
		out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		//Answer 2
		out.println("2. There are " + Crawler.numUrl + " unique page.");
		out.close();
		out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		//Answer 3
		out.println("3. There are " + Crawler.numSubDomain + " subdomains.");
		out.close();
		out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		printSubDomain();
		//Answer 4
		out.println ("4. The longest page is " + Crawler.longestPageUrl + ".");
		out.close();
		//Answer 5
		print500Words();

		return urlList;

	}

	private static void processEverything() throws FileNotFoundException
	{
		//URL list
		Scanner urlSrc = new Scanner(new File("url.txt"));
		while(urlSrc.hasNextLine())
		{
			String url = urlSrc.nextLine();
			++numUrl;
			urlList.add(url);
		}
		urlSrc.close();
		//Subdomain List
		Scanner subDomainSrc = new Scanner(new File("subdomain.txt"));
		while(subDomainSrc.hasNextLine())
		{
			String url = subDomainSrc.nextLine();
			//add to subdomainList 1)not found add new 2) increment the count
			if(!subDomainList.containsKey(url))
			{
				subDomainList.put(url, 1);
				++numSubDomain;
			}
			else
			{
				subDomainList.put(url, subDomainList.get(url)+1);
			}
		}
		subDomainSrc.close();
		//Commonword List
		Scanner wordSrc = new Scanner(new File("listofwords.txt"));
		while(wordSrc.hasNextLine())
		{
			String text = wordSrc.nextLine();
			processWord(text);
		}
		wordSrc.close();

		//Longest url
		Scanner longestUrlSrc = new Scanner(new File("longesturl.txt"));
		longestPageLength = 0;
		longestPageUrl = "";
		while(longestUrlSrc.hasNext())
		{
			String url = longestUrlSrc.next();
			Integer size = Integer.parseInt(longestUrlSrc.next());
			if(size > longestPageLength)
			{
				longestPageLength = size;
				longestPageUrl = url;
			}

		}
		longestUrlSrc.close();
		

	}


	public static void print500Words() throws Exception
	{
		wordListSort();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Answers.txt", true))) ;
		PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter("CommonWords.txt", true))) ;
		int i = 0;
		for(Map.Entry<String,Integer> entry : wordList.entrySet())
		{
			if(i>=500)
			{
				out.close();
				out2.close();
				return;
			}
			String key = entry.getKey();
			Integer value = entry.getValue();
			out.println(key+" : "+value);
			out2.println(key+" : "+value);
			++i;
		}
		out.close();
	}

	public static void printSubDomain() throws Exception
	{
		subDomainListSort();
		PrintWriter writer = new PrintWriter("Subdomains.txt");
		for(Map.Entry<String,Integer> entry : subDomainList.entrySet())
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			writer.println(key+" : "+value);
		}
		writer.close();
	}

	private static void processWord(String words)
	{
		Scanner  sc = new Scanner (words);
		while(sc.hasNext())
		{
			String word = sc.next();
			word = word.toLowerCase().replaceAll("[^a-z0-9']+", "");
			if(!stopWordsList.contains(word))
			{
				if(word.compareTo("") != 0)
				{
					Integer n = wordList.get(word);
					n = (n == null) ? 1: ++n;
					wordList.put(word, n);
				}
			}
		}
	}

	public static void subDomainListSort()
	{
		subDomainList = new TreeMap<String, Integer> (subDomainList);
	}

	public static void wordListSort()
	{
		List<Map.Entry<String, Integer>> list =
				new LinkedList<Map.Entry<String, Integer>>(wordList.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				if((o1.getValue()).compareTo(o2.getValue()) == 0)
				{
					return (o1.getKey()).compareTo(o2.getKey())*-1;
				}
				return (o1.getValue()).compareTo(o2.getValue())*-1;
			}
		});

		wordList = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			wordList.put(entry.getKey(), entry.getValue());
		}
	}

	public static void main(String[] args) throws Exception
	{
		crawl("http://www.ics.uci.edu/");
	}

}

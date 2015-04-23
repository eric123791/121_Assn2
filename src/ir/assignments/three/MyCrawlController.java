package ir.assignments.three;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


public class MyCrawlController  {
	private static final Logger logger = LoggerFactory.getLogger(MyCrawlController.class);

	public static void main(String[] args) throws Exception {
		/*if (args.length != 2) {
			logger.info("Needed parameters: ");
			logger.info("\t rootFolder (it will contain intermediate crawl data)");
			logger.info("\t numberOfCralwers (number of concurrent threads)");
			return;
		}*/
		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		String crawlStorageFolder = "test";

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = 5;		
		String userAgent = "UCI Inf141-CS121 crawler 49399981";
		int timeOut = 1000*60*15;

		CrawlConfig config = new CrawlConfig();
		config.setUserAgentString(userAgent);

		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(500);
		config.setResumableCrawling(false);
		config.setConnectionTimeout(timeOut);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed("http://www.ics.uci.edu/");
		controller.addSeed("http://www.ics.uci.edu/~lopes/");
		controller.addSeed("http://www.ics.uci.edu/~welling/");

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		
		long start = System.currentTimeMillis()/1000;
		
        controller.start(Crawler.class, numberOfCrawlers);  
        
        long end = System.currentTimeMillis()/1000;
        long totalTime = end - start;
		
	}
}
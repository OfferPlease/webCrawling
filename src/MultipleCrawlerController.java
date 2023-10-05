

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class MultipleCrawlerController {
    private static final Logger logger = LoggerFactory.getLogger(MultipleCrawlerController.class);

    public static void main(String[] args) throws Exception {
        // The folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        String crawlStorageFolder = "/tmp/crawler4j/";

        CrawlConfig config1 = new CrawlConfig();


        // The two crawlers should have different storage folders for their intermediate data.
        config1.setCrawlStorageFolder(crawlStorageFolder + "/crawler1");


        config1.setPolitenessDelay(500);
        config1.setMaxPagesToFetch(20000);
        config1.setMaxDepthOfCrawling(16);


        // We will use different PageFetchers for the two crawlers.
        PageFetcher pageFetcher1 = new PageFetcher(config1);


        // We will use the same RobotstxtServer for both of the crawlers.
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher1);

        CrawlController controller1 = new CrawlController(config1, pageFetcher1, robotstxtServer);


        CrawlerStatistics statistics = new CrawlerStatistics();

        controller1.addSeed("https://www.usatoday.com/");


        CrawlController.WebCrawlerFactory<BasicCrawler> factory1 = () -> new BasicCrawler(statistics);

        // The first crawler will have 5 concurrent threads and the second crawler will have 7 threads.
        controller1.startNonBlocking(factory1, 5);


        controller1.waitUntilFinish();
        logger.info("Crawler 1 is finished.");

    }
}
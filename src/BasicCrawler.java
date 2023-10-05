import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.parser.BinaryParseData;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BasicCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(BasicCrawler.class);

    private static final Pattern filters = Pattern.compile(
            ".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private static final Pattern binaryPatterns = Pattern.compile(".*(\\.(doc|pdf|bmp|gif|jpe?g|png|tiff?))$");
    private final String myDomain;
    private final CrawlerStatistics statistics;
    public BasicCrawler(CrawlerStatistics statistics) {
        this.statistics = statistics;
        this.myDomain = "https://www.usatoday.com/";
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        String originUrl = url.getURL();
        String status = "OK";
        if (filters.matcher(href).matches()) {
            status = "N_OK";
        }
        // skip .json files
        if(referringPage.getContentType().contains("application.json")){
            status = "N_OK";
        }

        if(!href.startsWith(myDomain)){
            status = "N_OK";
        };
        List<String> input = Arrays.asList(originUrl, status);
        statistics.writeToUrl_newSite(input);
        return status.equals("OK");
    }

    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        int parentDocid = page.getWebURL().getParentDocid();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Docid of parent page: {}", parentDocid);
        int numLinks = 0;
        int size = 0;
        String contentType = null;
        // deal with binary data
        if (binaryPatterns.matcher(url).matches() || ((page.getParseData() instanceof BinaryParseData))) {
            size = page.getContentData().length;
            contentType = page.getContentType();
        }
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            numLinks = links.size();
            size = page.getContentData().length;
            contentType = page.getContentType();
            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
            logger.debug("Number of outgoing links: {}", links.size());
        }
        List<String> input = Arrays.asList(url, String.valueOf(size), String.valueOf(numLinks), contentType);
        statistics.writeToVisit_newSite(input);
        logger.debug("=============");
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        // write fetched url and status code
        List<String> rowData =Arrays.asList(webUrl.getURL(), String.valueOf(HttpStatus.SC_OK));
        statistics.writeTo_newSite(rowData);
        if(statusCode != HttpStatus.SC_OK){
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.warn("Broken link: {}, this link was found in page: {}", webUrl.getURL(),
                        webUrl.getParentUrl());
            } else {
                logger.warn("Non success status for link: {} status code: {}, description: ",
                        webUrl.getURL(), statusCode, statusDescription);
            }
        }
    }


}
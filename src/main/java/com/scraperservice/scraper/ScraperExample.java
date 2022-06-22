package com.scraperservice.scraper;

import com.scraperservice.PreferConnectionType;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import com.scraperservice.utils.HTMLUtil;
import com.scraperservice.utils.ScrapeLinkUtil;
import com.scraperservice.utils.ScrapeUtil;
import com.scraperservice.utils.TableUtil;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PreferConnectionType(PreferConnectionType.ConnectionType.JsoupConnection)
public class ScraperExample extends Scraper {
    /*
         It's only example. This site doesn't contain such elements like NAME_SELECTOR, CATEGORY_SELECTOR etc.
     */
    private static final String BASE_SITE_URL = "https://example.com";

    private static final String CATEGORY_SELECTOR = "a.category";
    private static final String SUBCATEGORY_SELECTOR = "a.sub_category";
    private static final String PRODUCT_SELECTOR = "a.product";
    private static final String NEXT_PAGE_SELECTOR = "li.active + li > a";
    private static final String IS_CATEGORY_SELECTOR = String.join(",\n", CATEGORY_SELECTOR, SUBCATEGORY_SELECTOR, PRODUCT_SELECTOR);

    private static final String NAME_SELECTOR = "";
    private static final String IMAGE_SELECTOR = "";
    private static final String DESCRIPTION_SELECTOR = "";
    private static final String SPECIFICATION_SELECTOR = "table.specification";

    @Override
    public List<DataArray> scrapeData(PageData pageData) {
        Document document = pageData.getHtml();
        DataArray dataArray = new DataArray(pageData.getUrl());
        dataArray.add(new DataCell("name", ScrapeUtil.getText(document, NAME_SELECTOR)));
        dataArray.add(new DataCell("image", String.join(",\n",
                ScrapeLinkUtil.scrapeImages(document, IMAGE_SELECTOR, BASE_SITE_URL))));
        dataArray.add(new DataCell("description", new HTMLUtil(ScrapeUtil.getOuterHTML(document, DESCRIPTION_SELECTOR))
                .removeCommentsAttrAndEmptyLines().toString()));
        dataArray.add(new DataCell("specification", TableUtil.topHeadTable(document.selectFirst(SPECIFICATION_SELECTOR))));
        return Collections.singletonList(dataArray);
    }

    @Override
    public PageType getPageType(PageData pageData) {
        Document document = pageData.getHtml();
        if(document.selectFirst(NAME_SELECTOR) != null)
            return PageType.PRODUCT_PAGE;
        else if(document.selectFirst(IS_CATEGORY_SELECTOR) != null)
            return PageType.CATEGORY_PAGE;
        else
            return PageType.UNDEFINED;
    }

    @Override
    public List<String> getStartLinks() {
        return Collections.singletonList("https://example.com/products");
    }

    @Override
    public List<String> scrapeCategories(PageData pageData) {
        return ScrapeLinkUtil.scrapeLinks(pageData.getHtml(), CATEGORY_SELECTOR, BASE_SITE_URL);
    }

    @Override
    public List<String> scrapeSubCategories(PageData pageData) {
        return ScrapeLinkUtil.scrapeLinks(pageData.getHtml(), SUBCATEGORY_SELECTOR, BASE_SITE_URL);
    }

    @Override
    public List<String> scrapeLinksToProductPages(PageData pageData) {
        return ScrapeLinkUtil.scrapeLinks(pageData.getHtml(), PRODUCT_SELECTOR, BASE_SITE_URL);
    }

    @Override
    public String goToNextPage(PageData pageData) {
        return ScrapeLinkUtil.scrapeLink(pageData.getHtml(), NEXT_PAGE_SELECTOR, BASE_SITE_URL);
    }
}

package com.scraperservice.scraper.page;

public enum PageType {
    CATEGORY_PAGE,
    PRODUCT_PAGE,
    CATEGORY_AND_PRODUCT_PAGE,
    UNDEFINED;

    public boolean isProduct() {
        return this == PRODUCT_PAGE || this == CATEGORY_AND_PRODUCT_PAGE;
    }

    public boolean isCategory() {
        return this == CATEGORY_PAGE || this == CATEGORY_AND_PRODUCT_PAGE;
    }
}

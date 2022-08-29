package com.scraperservice.scraper.page;

import org.jsoup.nodes.Document;

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

    public boolean isUndefined() { return this == UNDEFINED; }

    /**
     * Выбирает тип страницы по найденным элементам. Сперва проверяется селектор с именем, а потом с элементами на страницах
     * категорий. Метод не определяет страницы, которые относятся сразу к нескольким типам
     * @param document html document
     * @param productNameSelector селектор указывающий на имя. Не должен находится на страницах категорий
     * @param categorySelector селектор указывающий на элементы, которые всегда есть на страницах категорий. Так же
     *                         могут встречаться и на страницах продуктов
     */
    public static PageType initPageType(Document document, String productNameSelector, String categorySelector) {
        if(document.selectFirst(productNameSelector) != null)
            return PageType.PRODUCT_PAGE;
        else if(document.selectFirst(categorySelector) != null)
            return PageType.CATEGORY_PAGE;
        else
            return PageType.UNDEFINED;
    }
}

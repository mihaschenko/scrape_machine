package com.scraperservice.scraper;

import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import com.scraperservice.utils.DocumentParser;
import com.scraperservice.utils.RegexUtil;
import com.scraperservice.utils.ScrapeUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

public class SwappieParser implements DocumentParser {
    private static final String MODEL_SELECTOR = "h1.lcDIta";
    private static final String STORAGE_SELECTOR = "div[class*=\"PhoneDetails__PhoneDetailsRowsContainer\"] span.lcDIta";
    private static final String ANSWER_SELECTOR = "div[class*=\"SummaryInfo__ContentWrapper\"] div[class*=\"AnswerRow__Container\"] > div:first-child";
    private static final String PRICE_SELECTOR = "[class*=\"PriceEstimate__EstimatePrice\"]";

    @Override
    public Set<DataArray> parseData(String url, Document document) {
        DataArray dataArray = new DataArray(url, false);
        dataArray.add(new DataCell("phone model", ScrapeUtil.getText(document, MODEL_SELECTOR)));
        dataArray.add(new DataCell("storage", ScrapeUtil.getText(document, STORAGE_SELECTOR)));

        Map<String, String> parameters = new HashMap<>();
        for(Element element : document.select(ANSWER_SELECTOR)) {
            List<String> nameAndValue = ScrapeUtil.getTexts(element, "span");
            if(nameAndValue.size() == 2) {
                String question = nameAndValue.get(0).trim();
                String answer = nameAndValue.get(1).trim();

                switch (answer) {
                    case "Ja":
                        answer = "Yes";
                        break;
                    case "Nee":
                        answer = "No";
                        break;
                    case "Onmerkbaar":
                        answer = "Inconspicuous";
                        break;
                    case "Merkbaar":
                        answer = "Noticeable";
                        break;
                }

                switch (question) {
                    case "Werkt de telefoon normaal?":
                        parameters.put("function", answer);
                        break;
                    case "Is het beeldscherm in orde en zonder beschadigingen?":
                        parameters.put("display", answer);
                        break;
                    case "Zitten er deuken of krassen op de behuizing of op het scherm?":
                        parameters.put("dents or", answer);
                        break;
                               /*case "Zitten er barsten in de glazen onderdelen (achterglas, camera lens)?":
                                   parameters.put("", answer);
                                   break;*/
                    case "Zitten er barsten of deuken in de behuizing / is de behuizing verbogen, heeft de telefoon waterschade, of is de vingerafdruksensor kapot?":
                        parameters.put("wet", answer);
                        parameters.put("cracks", answer);
                        break;
                }
            }
        }

        dataArray.add(new DataCell("function", parameters.getOrDefault("function", "-")));
        dataArray.add(new DataCell("display", parameters.getOrDefault("display", "-")));
        dataArray.add(new DataCell("dents or", parameters.getOrDefault("dents or", "-")));
        dataArray.add(new DataCell("cracks", parameters.getOrDefault("cracks", "-")));
        dataArray.add(new DataCell("wet", parameters.getOrDefault("wet", "-")));
        dataArray.add(new DataCell("price", RegexUtil.findText("[0-9]+",
                ScrapeUtil.getText(document, PRICE_SELECTOR))));
        return Collections.singleton(dataArray);
    }
}

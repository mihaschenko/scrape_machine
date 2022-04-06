import com.scraperservice.ScraperSetting;
import org.junit.Assert;
import org.junit.Test;

public class ScraperSettingTest {
    @Test
    public void scraperSettingTest() throws Exception {
        ScraperSetting scraperSetting = new ScraperSetting();
        scraperSetting.init();
        Assert.assertNotNull(scraperSetting.getScraper());
        System.out.println("Scraper name: " + scraperSetting.getScraper().getClass().getSimpleName());
        Assert.assertNotNull(scraperSetting.getConnectionClass());
        System.out.println("Connection name: " + scraperSetting.getConnectionClass().getSimpleName());
        Assert.assertNotEquals(scraperSetting.getStartLinks().size(), 0);
        System.out.println("Start links: ");
        scraperSetting.getStartLinks().forEach(link -> System.out.println("\t" + link));
    }
}

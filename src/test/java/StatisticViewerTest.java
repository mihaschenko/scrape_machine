import com.scraperservice.manager.StatisticManager;
import com.scraperservice.view.StatisticTextArea;
import com.scraperservice.view.StatisticFrame;
import org.junit.Test;

public class StatisticViewerTest {
    @Test
    public void statisticViewTest() {
        StatisticFrame statisticFrame = new StatisticFrame();
        String text = StatisticManager.getInstance().toString();
        StatisticTextArea statisticTextArea = new StatisticTextArea();
        statisticTextArea.setText(text);
        statisticFrame.add(statisticTextArea);
    }
}

import com.scraperservice.scraper.helper.LogHelper;
import org.junit.Test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogTest {
    @Test
    public void logTest() {
        Logger logger = LogHelper.getLogger();
        logger.log(Level.FINEST, "Log.test.FINEST");
        logger.log(Level.FINER, "Log.test.FINER");
        logger.log(Level.FINE, "Log.test.FINE");
        logger.log(Level.INFO, "Log.test.INFO");
        logger.log(Level.WARNING, "Log.test.WARNING");
        logger.log(Level.SEVERE, "Log.test.SEVERE");
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers)
            System.out.println(handler.getClass().getSimpleName());
    }
}

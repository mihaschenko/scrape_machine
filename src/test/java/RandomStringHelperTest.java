import com.scraperservice.utils.RandomStringHelper;
import org.junit.Assert;
import org.junit.Test;

public class RandomStringHelperTest {
    @Test
    public void randomStringTest() {
        for(int i = 0; i < 500; i++) {
            String randomString = RandomStringHelper.getRandomStringOnlyLetters(10);
            System.out.println(randomString);
            Assert.assertTrue(randomString.matches("[a-zA-Z]{10}"));
        }
    }
}

import com.scraperservice.queue.UniqueValuesStorage;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class UniqueValueStorageTest {
    @Test
    public void uniqueValueStorageTest() throws Exception {
        try(UniqueValuesStorage uniqueValuesStorage = new UniqueValuesStorage()) {
            boolean result;
            result = uniqueValuesStorage.checkUniqueValue("First");
            Assert.assertTrue(result);
            result = uniqueValuesStorage.checkUniqueValue("Second");
            Assert.assertTrue(result);
            result = uniqueValuesStorage.checkUniqueValue("Third");
            Assert.assertTrue(result);
            result = uniqueValuesStorage.checkUniqueValue("Second");
            Assert.assertFalse(result);
            result = uniqueValuesStorage.checkUniqueValue("First");
            Assert.assertFalse(result);
            result = uniqueValuesStorage.checkUniqueValue("Fourth");
            Assert.assertTrue(result);
            result = uniqueValuesStorage.checkUniqueValue("First");
            Assert.assertFalse(result);

            Method method = UniqueValuesStorage.class.getDeclaredMethod("showAllUniqueValues");
            method.setAccessible(true);
            method.invoke(uniqueValuesStorage);
        }
    }
}

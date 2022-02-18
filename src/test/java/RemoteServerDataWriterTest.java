import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import com.scraperservice.storage.writer.RemoteServerDataWriter;
import org.junit.Test;

import java.util.Collections;

public class RemoteServerDataWriterTest {
    @Test
    public void testRequest() throws Exception {
        RemoteServerDataWriter remoteServerDataWriter = new RemoteServerDataWriter("", "");
        remoteServerDataWriter.writeData(Collections.singletonList(getTestDataArray()));
    }

    private DataArray getTestDataArray() {
        DataArray dataArray = new DataArray("somethingUrl");
        dataArray.add(new DataCell("name1", "value1"));
        dataArray.add(new DataCell("name2", "value2"));
        dataArray.add(new DataCell("name3", "value3"));
        dataArray.add(new DataCell("name4", "value4"));
        dataArray.add(new DataCell("name5", "value5"));
        dataArray.add(new DataCell("name6", "value6"));
        dataArray.add(new DataCell("name7", "value7"));
        dataArray.add(new DataCell("name8", "value8"));
        dataArray.add(new DataCell("name9", "value9"));
        return dataArray;
    }
}

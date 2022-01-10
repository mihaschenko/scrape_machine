import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.proxy.ProxyProperty;
import com.scraperservice.utils.ScrapeUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

public class ProxyTest {
    @Test
    public void proxyTestWithoutAuthentication() throws IOException {
        final String host = "us-10m.geosurf.io";
        final String port = "10001";
        ProxyProperty.setAllProxyProperty(host, port);
        showIpAddress();
    }

    private void showIpAddress() throws IOException {
        final String ipSelector = "table p > font[face=\"Verdana, Arial, Helvetica, sans-serif\"]";
        JsoupConnection connection = new JsoupConnection();
        Document document = connection.getPage("https://www.ipchicken.com");
        System.out.println(ScrapeUtils.getText(document, ipSelector));
    }
}

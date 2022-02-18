import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.proxy.ProxyAuthenticator;
import com.scraperservice.proxy.ProxyProperty;
import com.scraperservice.utils.ScrapeUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.net.Authenticator;

public class ProxyTest {
    public void proxyTestJsoup() throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.scraperservice.proxy");
        ProxyProperty proxyProperty = context.getBean(ProxyProperty.class);
        ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setUseProxy(true);
        connectionProperties.setProxyProperty(proxyProperty);
        ProxyAuthenticator proxyAuthenticator = context.getBean(ProxyAuthenticator.class);

        ProxyProperty.setAllProxyProperty(proxyProperty.getHost(), Integer.toString(proxyProperty.getPort()));
        ProxyProperty.setPropertyAuthDisabledSchemesToEmpty();

        System.out.println("Host: " + proxyProperty.getHost() + " Port: " + proxyProperty.getPort() + "\n"
            + "User: " + proxyAuthenticator.getUser() + " Password: " + proxyAuthenticator.getPassword());

        Authenticator.setDefault(proxyAuthenticator);
        JsoupConnection connection = new JsoupConnection();
        Document document = connection.getPage("https://www.ipchicken.com", connectionProperties);
        System.out.println(ScrapeUtils.getText(document, "table p > font[face=\"Verdana, Arial, Helvetica, sans-serif\"]"));
    }

    @Test
    public void proxyTestSelenium() throws Exception {
        try(SeleniumConnection connection = new SeleniumConnection()) {
            Document document = connection.getPage("https://www.ipchicken.com");
            System.out.println(ScrapeUtils.getText(document, "table p > font[face=\"Verdana, Arial, Helvetica, sans-serif\"]"));
        }
    }
}

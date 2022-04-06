package com.scraperservice.storage.writer;

import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RemoteServerDataWriter implements ScraperDataWriter {
    static {
        Set<String> artifactoryLoggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
        for(String log:artifactoryLoggers) {
            ch.qos.logback.classic.Logger artLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(log);
            artLogger.setLevel(ch.qos.logback.classic.Level.INFO);
            artLogger.setAdditive(false);
        }
    }

    private final String key;
    private final String url;

    public RemoteServerDataWriter(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Override
    public boolean writeData(List<DataArray> dataArrayList) throws IOException {
        for(DataArray dataArray : dataArrayList) {
            HttpClient httpclient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> params = new ArrayList<>();
            for(DataCell dataCell : dataArray)
                params.add(new BasicNameValuePair(dataCell.getName(), dataCell.getValue()));
            params.add(new BasicNameValuePair("key", key));

            //for(NameValuePair pair : params)
            //    System.out.println(pair.getName() + "\n\t" + pair.getValue());

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httpPost);

            /*HttpEntity entity = response.getEntity();
            if (entity != null) {
                StatusLine statusLine = response.getStatusLine();
                System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.println("Response body: " + responseBody);
            }*/
        }
        return true;
    }

    @Override
    public void close() throws Exception {}
}

package com.scraperservice.storage.writer;

import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RemoteServerDataWriter implements ScraperDataWriter {
    private final String key;
    private final String url;

    public RemoteServerDataWriter(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Override
    public boolean writeData(List<DataArray> dataArrayList) throws Exception {
        for(DataArray dataArray : dataArrayList) {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<>();
            for(DataCell dataCell : dataArray)
                params.add(new BasicNameValuePair(dataCell.getName(), dataCell.getValue()));
            params.add(new BasicNameValuePair("key", key));

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
        }
        return true;
    }

    @Override
    public void close() throws Exception {}
}

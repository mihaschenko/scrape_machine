package com.scraperservice.app;

import com.scraperservice.app.database.ConfigRunDAO;
import com.scraperservice.spring.SpringJdbcConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        //int configId = run.getConfigIdByHash(req.getParameter("hash"));

        //System.out.println("CONFIG ID - " + configId);
        System.out.println("----------");
        //Runtime rt = Runtime.getRuntime();
        //Process pr = rt.exec("java -jar map.jar time.rel test.txt debug");
    }
}

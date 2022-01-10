package com.scraperservice.manager;

import com.scraperservice.proxy.ProxyAuthenticator;
import com.scraperservice.proxy.ProxyProperty;

import java.io.FileInputStream;
import java.net.Authenticator;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ProxyManager {
    private final static ProxyManager proxyManager = new ProxyManager();

    private final String host;
    private final int startPort;
    private final int finishPort;
    private final String login;
    private final String password;

    public static ProxyManager getInstance() {
        return proxyManager;
    }

    private ProxyManager() {
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/proxy.properties"));

            host = properties.getProperty("proxy.host");
            String port = properties.getProperty("proxy.port");
            login = properties.getProperty("proxy.name");
            password = properties.getProperty("proxy.password");
            if(port.matches("[0-9]+")) {
                int portInt = Integer.parseInt(port);
                startPort = portInt;
                finishPort = portInt;
            }
            else if(port.matches("[0-9]+\\|[0-9]+")) {
                String[] portRange = port.split("\\|");
                startPort = Integer.parseInt(portRange[0]);
                finishPort = Integer.parseInt(portRange[1]);
                if(startPort > finishPort)
                    throw new IllegalArgumentException("port range is incorrect. start port range = " + startPort
                            + ". finish port range = " + finishPort);
            }
            else
                throw new IllegalArgumentException("proxy port is not set");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProxyProperties() {
        int port = new Random().nextInt(startPort, finishPort+1);
        ProxyProperty.setAllProxyProperty(host, Integer.toString(port));
        Authenticator.setDefault(new ProxyAuthenticator(login, password));
        if(startPort != finishPort) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    changePort();
                }
            }, 5000, 5000);
        }
    }

    private void changePort() {
        int port = new Random().nextInt(startPort, finishPort+1);
        ProxyProperty.setPort(Integer.toString(port));
    }
}

package com.scraperservice.connection.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator {
    private final String user;
    private final String password;

    public ProxyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        if (getRequestorType() == RequestorType.PROXY)
            return new PasswordAuthentication(user, password.toCharArray());
        return super.getPasswordAuthentication();
    }
}

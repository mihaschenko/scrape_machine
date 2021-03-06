package com.scraperservice.proxy;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Data
public class ProxyAuthenticator extends Authenticator {
    private final String user;
    private final String password;

    public ProxyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (getRequestorType() == RequestorType.PROXY)
            return new PasswordAuthentication(user, password.toCharArray());
        return super.getPasswordAuthentication();
    }
}

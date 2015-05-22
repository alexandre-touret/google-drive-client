package info.touret.googledriveclient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by touret-a on 20/05/2015.
 */
public class ProxyAuthenticator extends Authenticator{
    private PasswordAuthentication auth;

    public ProxyAuthenticator(String user, String password) {
        auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
    }
}

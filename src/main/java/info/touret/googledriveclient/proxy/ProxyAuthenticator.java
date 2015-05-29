package info.touret.googledriveclient.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Gere l'identification aupres du proxy
 * Created by touret-a on 20/05/2015.
 */
public class ProxyAuthenticator extends Authenticator{
    private PasswordAuthentication auth;

    public ProxyAuthenticator(String user, String password) {
        auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
    }
}

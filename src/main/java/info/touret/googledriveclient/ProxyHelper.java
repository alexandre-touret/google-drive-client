package info.touret.googledriveclient;

import org.apache.commons.codec.binary.Base64;

import java.net.Authenticator;

/**
 *
 * Created by touret-a on 20/05/2015.
 */
public class ProxyHelper {

    private Boolean useHTTPProxy;
    private Boolean useHTTPAuth;

    public ProxyHelper(Boolean useHTTPProxy, Boolean useHTTPAuth, String HTTPPort, String HTTPHost, String HTTPUsername, String HTTPPassword) {
        this.useHTTPProxy = useHTTPProxy;
        this.useHTTPAuth = useHTTPAuth;
        this.HTTPPort = HTTPPort;
        this.HTTPHost = HTTPHost;
        this.HTTPUsername = HTTPUsername;
        this.HTTPPassword = HTTPPassword;
    }

    public ProxyHelper() {
    }

    public Boolean getUseHTTPProxy() {
        return useHTTPProxy;
    }

    public void setUseHTTPProxy(Boolean useHTTPProxy) {
        this.useHTTPProxy = useHTTPProxy;
    }

    public Boolean getUseHTTPAuth() {
        return useHTTPAuth;
    }

    public void setUseHTTPAuth(Boolean useHTTPAuth) {
        this.useHTTPAuth = useHTTPAuth;
    }

    public String HTTPPort;
    public String HTTPHost;

    public String getHTTPPort() {
        return HTTPPort;
    }

    public void setHTTPPort(String HTTPPort) {
        this.HTTPPort = HTTPPort;
    }

    public String getHTTPHost() {
        return HTTPHost;
    }

    public void setHTTPHost(String HTTPHost) {
        this.HTTPHost = HTTPHost;
    }


    private String  HTTPUsername;
    private String HTTPPassword;

    public String getHTTPUsername() {
        return HTTPUsername;
    }

    public void setHTTPUsername(String HTTPUsername) {
        this.HTTPUsername = HTTPUsername;
    }

    public String getHTTPPassword() {
        return HTTPPassword;
    }

    public void setHTTPPassword(String HTTPPassword) {
        this.HTTPPassword = HTTPPassword;
    }

    public void setUpProxy() {
        if (getUseHTTPProxy()) {
            // HTTP/HTTPS Proxy
            System.setProperty("http.proxyHost", getHTTPHost());
            System.setProperty("http.proxyPort", getHTTPPort());
            System.setProperty("https.proxyHost", getHTTPHost());
            System.setProperty("https.proxyPort", getHTTPPort());
            if (getUseHTTPAuth()) {
                String encoded = new String(Base64.encodeBase64((getHTTPUsername() + ":" + getHTTPPassword()).getBytes()));
                //con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                Authenticator.setDefault(new ProxyAuthenticator(getHTTPUsername(), getHTTPPassword()));
            }
        }

    }
}

package app.helpers;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlHelper {
    public static String getCurrentUrl(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        URL url = new URL(request.getRequestURL().toString());
        String host  = url.getHost();
        String userInfo = url.getUserInfo();
        String scheme = url.getProtocol();
        int port = url.getPort();
        String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
        String query = (String) request.getAttribute("javax.servlet.forward.query_string");

        URI uri = new URI(scheme,userInfo,host,port,path,query,null);
        return uri.toString();
    }
}

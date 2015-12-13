package newspaper;

import Common.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


/**
 * Created by Light on 13/12/15.
 */
public class Newspaper {

    private URL url;
    private List<String> relevantContentTag;
    private String urlContent;
    
    public Newspaper(String url){
        try {
            this.url = new URL(url);
            this.urlContent = "";
            this.getWebConent();
            relevantContentTag = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            this.url = null;
            this.urlContent = null;
            relevantContentTag = null;
        }
    }

    public void setRelevantContentTag(List<String> tags){
        relevantContentTag = tags;
    }
    private void getWebConent() {
        if(url != null) {
            BufferedReader in = null;
            try {
                URLConnection urlConn = url.openConnection();
                in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    urlContent = urlContent.concat(inputLine).concat(Utils.END_OF_LINE);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getUrlContent(){
        return urlContent;
    }

    //Main for testing
    public static void main(String[] args){
        System.out.println(new Newspaper("http://www.elmundo.es/salud.html").getUrlContent());
    }
}

package org.perf;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.*;

import java.util.*;

public class Runner implements Callable<HashMap<String, String>> {

    private String url = null;
    private String method = null;

    public Runner(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public HashMap<String, String> call() throws Exception {
        try {
            URL obj = new URL(url);
            long startAt = new Date().getTime();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            long endAt = new Date().getTime();
            long establishmentTime = endAt - startAt; 
    
            con.setRequestMethod(method);
    
            startAt = new Date().getTime();
            int responseCode = con.getResponseCode();
            endAt = new Date().getTime();
        
            long ttfbTime = endAt - startAt;
    
            startAt = new Date().getTime();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            endAt = new Date().getTime();
            long contentDownloadTime = endAt - startAt;
     
            HashMap<String, String> res = new HashMap<String, String>();
            res.put("ttfb", Long.toString(ttfbTime));
            res.put("cdt", Long.toString(contentDownloadTime));
            res.put("estb", Long.toString(establishmentTime));
            res.put("code", Integer.toString(responseCode));
            res.put("total", Long.toString(ttfbTime + contentDownloadTime + establishmentTime));
        
            System.out.println("Result: " + res.toString());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

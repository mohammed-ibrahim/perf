package org.perf;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;
import javax.net.ssl.HttpsURLConnection;

import java.util.*;

public class Perf {
    public void start(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: program <config_file>");
            return;
        }

        File file = new File(args[0]); 
        if (!file.exists()) {
            System.out.println("Usage: program <config_file>");
            return;
        }
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));

        int nThreads = Integer.parseInt(prop.getProperty("num_of_calls"));
        String method = prop.getProperty("method");
        List<String> urls = getUrls(prop.getProperty("url_list"));

        ExecutorService service = Executors.newFixedThreadPool(10);

        Random random = new Random();
        System.out.println("Scheduling the number of threads: " + Integer.toString(nThreads));
        for (int i =0; i<nThreads; i++) {
            int randomItem = random.nextInt(urls.size());
            String randomUrl = urls.get(randomItem);
            service.submit(new Runner(randomUrl, method));
        }

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws Exception {
        Perf p = new Perf();
        p.start(args);
    }

    private List<String> getUrls(String urls) {
        List<String> li = new ArrayList<String>();

        for (String str: urls.split(",")) {
            li.add(str);
        }

        return li;
    }

    private double calculateAverage(List <Long> score) {
        Long sum = new Long(0);
        if(!score.isEmpty()) {
            for (Long mark : score) {
                sum += mark;
            }
            return sum.doubleValue() / score.size();
        }
        return sum;
    }
}

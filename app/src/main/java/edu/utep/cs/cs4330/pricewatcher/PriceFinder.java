package edu.utep.cs.cs4330.pricewatcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PriceFinder {

    private String price;
    private String response;
    private String[] html;

    //Gets the price of an item from its url
    protected String getInformation(String url) {
        new Thread(() -> {
            StringBuilder content = new StringBuilder();
            String reader = "";
            try {
                URL web_url = new URL(url);
                HttpURLConnection httpConn = (HttpURLConnection) web_url.openConnection();
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (HttpURLConnection.HTTP_OK == httpConn.getResponseCode()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    while ((reader = input.readLine()) != null) { content.append(reader); }
                }

                response = content.toString();
                html = response.split(",");

                if(web_url.toString().contains("bestbuy")) {
                    for (String s : html) {
                        if (s.contains("price\":")) { price = s.substring(9, s.length() - 1); }
                    }
                }
                if(web_url.toString().contains("walmart")) {
                    for (String s : html) {
                        if (s.contains("unitPrice")) { price = s.substring(12); }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
        return price;
    }

    //Finds the price of the current item
    protected int findPrice(String url) {
        int min = 100;
        int max = 500;
        return (int) (Math.random() * (max - min + 1) + min);
    }
    //Finds the percentage difference between the current and initial price
    protected int priceChange(double curr, double init) { return (int) (((curr - init) * 100) / init); }

}

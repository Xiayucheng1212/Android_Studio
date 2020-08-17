import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;


class Data{
    String name ;
    String pic;
    String rate;
    // 還需要什麼資訊？？
    public Data(String name, String pic, String rate ){
        this.name = name;
        this.pic = pic;
        this.rate = rate;
    }
}

public class User {
    double lat;
    double lon;
    String cuisine;
    //cuisine 選項
    //構造函數
    public User(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
        this.cuisine = "";

    }
    public User(double lat, double lon, String cuisine){
        this.lat = lat;
        this.lon = lon;
        this.cuisine = cuisine;

    }
    //獲取資料api
    public List getData(){
        List<Data> data = new ArrayList<>();
        String url = cuisine == "" ? String.format("https://www.foodpanda.com.tw/restaurants/new?lat=%.7f&lng=%.7f&vertical=restaurants", lat, lon): String.format("https://www.foodpanda.com.tw/restaurants/new?lat=%.7f&lng=%.7f&vertical=restaurants&cuisines=%s", lat, lon, cuisine);
        try {
            Document doc = Jsoup.connect(url).get();
            // link還需要重新連結到商店頁面
            Elements links = doc.select(".vendor-list li a");

            Elements shops = doc.select(".vendor-list .name");
            Elements rating = doc.select(".vendor-list .rating b");

//          Element & Elements are different!!
            Element thisOne = null;
            int index = 0;
            // 一開始背景狀態下先開始取得資料
            for(int i = 0; i < 6; i++){
                String name = shops.get(i).text();
                String rate = shops.get(i).text();
                Document doc1 = Jsoup.connect(("https://www.foodpanda.com.tw"+links.get(i).attr("href"))).get();
                String [] pics_url = doc1.select(".hero-banner").attr("data-src").split("\\?");
                String pic = pics_url[0];
                DownloadImage(pic, new File("").getAbsolutePath()+"/src/shop"+i+".jpeg");
                Data shop_data = new Data(name, "shop"+i+".jpeg", rate);
                data.add(shop_data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        return data;
    }
    //下載圖片
    public static void DownloadImage(String search, String path) {

        // This will get input data from the server
        InputStream inputStream = null;

        // This will read the data from the server;
        OutputStream outputStream = null;

        try {
            // This will open a socket from client to server
            URL url = new URL(search);

            // This user agent is for if the server wants real humans to visit
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

            // This socket type will allow to set user_agent
            URLConnection con = url.openConnection();

            // Setting the user agent
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Requesting input data from server
            inputStream = con.getInputStream();

            // Open local file writer
            outputStream = new FileOutputStream(path);

            // Limiting byte written to file per loop
            byte[] buffer = new byte[2048];

            // Increments file size
            int length;

            // Looping until server finishes
            while ((length = inputStream.read(buffer)) != -1) {
                // Writing data
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

        } catch (Exception ex) {
            System.out.println("failed");
        }

        // closing used resources
        // The computer will not be able to use the image
        // This is a must


    }
    public static void main(String[] argv) {
        //初始化user
        User user = new User(25.0011343, 121.5450372, "166");
        user.getData();
    }
}


package com.mikhaellopez.androidwebserver;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Mikhael LOPEZ on 14/12/2015.
 */
public class AndroidWebServer extends NanoHTTPD {
    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
       /* String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );*/
//        String answer = "";
//        try {
//            // Open file from SD Card
//            File root = Environment.getExternalStorageDirectory();
//            FileReader index = new FileReader(root.getAbsolutePath() + File.separator +
//                    "index.html");
//                    BufferedReader reader = new BufferedReader(index);
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                answer += line;
//            }
//        } catch(IOException ioe) {
//            Log.w("Httpd", ioe.toString());
//        }
//
//        return newFixedLengthResponse(answer);


        int len = 0;
        byte[] buffer = null;
        Log.d("Httpd", "client ip = " + session.getHeaders().get("remote-addr"));

        // 默认传入的url是以“/”开头的，需要删除掉，否则就变成了绝对路径
        String file_name = session.getUri().substring(1);
        Log.d("Httpd", "file_name = " + file_name);
        // 默认的页面名称设定为index.html
        if(file_name.equalsIgnoreCase("")){
            file_name = "index.html";
        }else if (TextUtils.equals(file_name, "easyshare.apk")){
            try {
                String answer = "";
                String filepath = App.getContext().getPackageManager()
                        .getApplicationInfo(BuildConfig.APPLICATION_ID, 0).sourceDir;
                File file = new File(filepath);
                Log.d("Httpd", "apk patch = " + filepath);
                FileInputStream fileInputStream = new FileInputStream(file);

                return newFixedLengthResponse(Response.Status.OK, "application/apk", fileInputStream, file.length());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Httpd", "e = " + e.getMessage());
            }
            return response404(session, session.getUri());
        }

        try {

            //通过AssetManager直接打开文件进行读取操作
            InputStream in = App.getContext().getAssets().open(file_name, AssetManager.ACCESS_BUFFER);

            //假设单个网页文件大小的上限是1MB
            buffer = new byte[1024*1024];

            int temp=0;
            while((temp=in.read())!=-1){
                buffer[len]=(byte)temp;
                len++;
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 将读取到的文件内容返回给浏览器
        return newFixedLengthResponse(new String(buffer,0,len));
    }
    public Response response404(IHTTPSession session,String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found "+url + " !");
        builder.append("</body></html>\n");
        return newFixedLengthResponse(builder.toString());
    }
}

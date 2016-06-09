package com.mikhaellopez.androidwebserver;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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


        Log.d("Httpd", "client ip = " + session.getHeaders().get("remote-addr"));

        // 默认传入的url是以“/”开头的，需要删除掉，否则就变成了绝对路径
        String fileName = session.getUri().substring(1);
        Log.d("Httpd", "fileName = " + fileName);
        // 默认的页面名称设定为index.html
        if (fileName.equalsIgnoreCase("")) {
            //fileName = "index.html";
            fileName = "t-share-files.html";
        } else if (TextUtils.equals(fileName, "easyshare.apk")) {
            try {
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
            return response404(session.getUri());
        }

        try {
            //通过AssetManager直接打开文件进行读取操作
            InputStream inputStream = App.getContext().getAssets().open(fileName,
                    AssetManager.ACCESS_BUFFER);

            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);
            inputStream.close();
            return newFixedLengthResponse(new String(buffer));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response404(session.getUri());
    }

    public Response response404(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found " + url + " !");
        builder.append("</body></html>\n");
        return newFixedLengthResponse(builder.toString());
    }
}

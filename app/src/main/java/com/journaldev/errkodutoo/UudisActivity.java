package com.journaldev.errkodutoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class UudisActivity extends Activity {

    private TextView txt_body;
    private String sUudisJSON;
    private ImageView bmp_Pilt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String sUrl;;
        String sServiceURL="https://services.err.ee/api/content/get/";
        String sContentID;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uudis_details);
        txt_body=(TextView)findViewById(R.id.txt_uudis_body);
        sContentID = getIntent().getStringExtra("content_id");

        if (1==2//kasFailEksisteerib(sContentID)
            )
        {
            loefailist(sContentID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txt_body.setText(Html.fromHtml(sUudisJSON, Html.FROM_HTML_MODE_COMPACT));
            } else {
                txt_body.setText(Html.fromHtml(sUudisJSON));
            }

        }else
        {
            sUrl=sServiceURL+sContentID;
            bmp_Pilt=(ImageView)findViewById(R.id.bmp_uudis);
            new LoadUudisRSS().execute(sUrl,sContentID);

        }




    }

    private boolean kasFailEksisteerib(String sf)
    {InputStream inputStream_I = null;
        boolean bRet;
        bRet=false;
        try {
            inputStream_I = getApplicationContext().openFileInput(sf);
            bRet=true;
            inputStream_I.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bRet;
    }
    private void loefailist(String sContentID) {
        FileInputStream inputStream;
        InputStreamReader inputreader;
        String path,line;
        path=sContentID;
        InputStream inputStream_I = null;
        sUudisJSON="";
        try {
            inputStream_I = getApplicationContext().openFileInput(path);
            inputreader = new InputStreamReader(inputStream_I);

            BufferedReader buffreader = new BufferedReader(inputreader);
            while (( line = buffreader.readLine()) != null)
            {
                sUudisJSON+=line;
            }
            buffreader.close();

        } catch (IOException e) {

            e.printStackTrace();
        }


    }
    private void loePilt(String sContentID)
    {
        Bitmap bitmap = null;
        try {
            InputStream imagestream =getApplicationContext().openFileInput(sContentID);
            bitmap = BitmapFactory.decodeStream(imagestream);
            imagestream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean kasfailOlemas(String sContentID) {
        boolean bRet=false;
        File file;
        String filePathTxt,sfilePathBMP;
        filePathTxt=sContentID ;
        sfilePathBMP=sContentID;
        file = new File(sContentID);
        if (file.exists())
        {
            bRet=true;
        }
        return bRet;
    }

    public String getXmlFromUrl(String url) {
        String xml = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
    public class LoadUudisRSS extends AsyncTask<String,String,String>{
        Bitmap bitmap;
        String sPhotoURL;
        String sContent;
        String body;
        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];
            sContent=args[1];
            sUudisJSON=getXmlFromUrl(rss_url);
            // siin tuleks JSNON ära töödelda, ja lugeda pilt sisse.
            try {
                JSONObject json = new JSONObject(sUudisJSON);
                JSONObject jsonResponse = json.getJSONObject("content");
                // String pealkiri = jsonResponse.getString("heading");
                // String lead = jsonResponse.getString("lead");
                 body = jsonResponse.getString("body");
                JSONArray photos = jsonResponse.getJSONArray("photos");
                JSONObject photoType=photos.getJSONObject(0).getJSONObject("photoTypes").getJSONObject("8");
                sPhotoURL= photoType.getString("url");



            } catch (JSONException e) {
                e.printStackTrace();
            }
            // loeme nüüd pildi
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(sPhotoURL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            doSalvestaUudis(body);
            doSalvestaPilt(bitmap,sContent);
            return sUudisJSON;
        }

        private void doSalvestaUudis(String sUudisJSON) {
            FileOutputStream outputStream = null;
            String fileName;
            File outputDir = getApplicationContext().getCacheDir();
            fileName=sContent;
            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(sUudisJSON.getBytes());
                outputStream.close();


            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        private void doSalvestaPilt(Bitmap bitmap, String filename)
        {
            try (
                FileOutputStream out = new FileOutputStream(filename)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            bmp_Pilt.setImageBitmap(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txt_body.setText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT));
            } else {
                txt_body.setText(Html.fromHtml(body));
            }

        }
        // abifunktsioonid




    }

}

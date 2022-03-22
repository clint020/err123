package com.journaldev.errkodutoo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {


    ArrayList<String> rssLinks = new ArrayList<>();
    Button btn_uudised;
    String sUUdisUrl="https://www.err.ee/rss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_uudised=(Button)findViewById(R.id.btnErrNews);
        btn_uudised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rssLinks.clear();
                rssLinks.add(sUUdisUrl);
                startActivity(new Intent(MainActivity.this, RSSFeedActivity.class).putExtra("rssLink", rssLinks.get(0)));
            }
        });
        rssLinks.clear();
        rssLinks.add(sUUdisUrl);
        startActivity(new Intent(MainActivity.this, RSSFeedActivity.class).putExtra("rssLink", rssLinks.get(0)));
    }


}

package com.journaldev.errkodutoo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RSSFeedActivity extends ListActivity {


    private ProgressBar pDialog;
    ArrayList<HashMap<String, String>> rssItemList = new ArrayList<>();

    RSSParser rssParser = new RSSParser();
    Button btn_varskenda;
    String rss_link;
    List<RSSItem> rssItems = new ArrayList<>();
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_CONTENT_ID="0";
    private static String TAG_IMG_ID="0";
    private static int ithumbnaildrawable=5;//R.drawable.ic_launcher_foreground;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed);
        btn_varskenda=(Button)findViewById(R.id.btn_varskenda);

        rss_link = getIntent().getStringExtra("rssLink");

        btn_varskenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadRSSFeedItems().execute(rss_link);
            }
        });
        new LoadRSSFeedItems().execute(rss_link);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                    //tuleb teha uus klass
                    Intent in = new Intent(getApplicationContext(), UudisActivity.class);
                    String contentid = ((TextView) view.findViewById(R.id.contentID)).getText().toString().trim();
                    in.putExtra("content_id", contentid); // siia tuleb panna contentID hoopis
                    startActivity(in);


            }
        });
    }

    public class LoadRSSFeedItems extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressBar(RSSFeedActivity.this, null, android.R.attr.progressBarStyleLarge);


            RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            pDialog.setLayoutParams(lp);
            pDialog.setVisibility(View.VISIBLE);
            relativeLayout.addView(pDialog);
        }

        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];

            // list of rss items
            rssItems = rssParser.getRSSFeedItems(rss_url);

            // looping through each item
            for (RSSItem item : rssItems) {
                // creating new HashMap
                if (item.link.toString().equals(""))
                    break;
                HashMap<String, String> map = new HashMap<String, String>();

                // adding each child node to HashMap key => value

                String givenDateString = item.pubdate.trim();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                try {
                    Date mDate = sdf.parse(givenDateString);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.US);
                    item.pubdate = sdf2.format(mDate);

                } catch (ParseException e) {
                    e.printStackTrace();

                }



                map.put(TAG_TITLE, item.title);
             //   map.put(TAG_LINK, item.link);
                map.put(TAG_PUB_DATE, item.pubdate); // If you want parse the date
                map.put(TAG_CONTENT_ID,item.contentid);
               // map.put(TAG_IMG_ID, Integer.toString(ithumbnaildrawable));

                // adding HashList to ArrayList
                rssItemList.add(map);

            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            RSSFeedActivity.this,
                            rssItemList, R.layout.rss_item_list_row,
                            new String[]{TAG_LINK,
                                    TAG_TITLE,
                                    TAG_PUB_DATE,
                                    TAG_CONTENT_ID

                            },//hashmap key
                            new int[]{R.id.page_url,
                                    R.id.title,
                                    R.id.pub_date,
                                    R.id.contentID
                            });//View id

                    // updating listview
                    SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object o, String s) {
                            if (view.equals((TextView) view.findViewById(R.id.contentID))) {
                                TextView txt_ContentView = (TextView) view.findViewById(R.id. contentID);
                                txt_ContentView.setVisibility(View.GONE);
                                //Change color/answer/etc for textView_5
                            }

                            //OR
                            if (view instanceof TextView) {
                                //Do stuff
                                ((TextView) view).setText(s);
                                return true;
                            }
                            return false;
                        }
                    };
                    ((SimpleAdapter) adapter).setViewBinder(binder);
                    setListAdapter(adapter);
                }
            });
            return null;
        }

        protected void onPostExecute(String args) {
            pDialog.setVisibility(View.GONE);
        }
    }
}

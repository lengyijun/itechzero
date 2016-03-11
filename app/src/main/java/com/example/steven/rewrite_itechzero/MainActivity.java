package com.example.steven.rewrite_itechzero;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.yuanban_listview)ListView yaunban_lv;
    @Bind(R.id.yuanban)TextView tv;
    Element ele_main;
    Google_adapter google_adapter;
    ArrayList<Link> linkArrayList=new ArrayList<Link>();
    Check_connection check_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        google_adapter=new  Google_adapter(getApplicationContext(), 0, linkArrayList);
        yaunban_lv.setAdapter(google_adapter);

        yaunban_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();

                Link link=google_adapter.getItem(position);
                Intent browerIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
                startActivity(browerIntent);
                yaunban_lv.getChildAt(position-yaunban_lv.getFirstVisiblePosition()).setBackgroundColor(Color.GRAY);
            }
        });
        OkHttpUtils.get()
                .url("http://www.itechzero.com/google-mirror-sites-collect.html")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        ele_main=doc.getElementsByClass("entry-content").last();
                        try {
                            parse_class(ele_main, "原版网页");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.origin_version) {
            tv.setText("原版网页");
            try {
                parse_class(ele_main, "原版网页");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.science) {
            tv.setText("谷歌学术");
            try {
                parse_class(ele_main, "谷歌学术");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.bianzhong) {
            tv.setText("变种网页");
            try {
                parse_class(ele_main, "变种网页");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.picture) {
            tv.setText("谷歌图片");
            try {
                parse_class(ele_main, "谷歌图片");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void parse_class(Element ele_main,String key) throws InterruptedException {
        if(check_connection!=null){
            check_connection.cancel(true);
        }
        if(linkArrayList!=null){
            linkArrayList.clear();
        }
        Elements head=ele_main.getElementsContainingOwnText(key);
        Element temp1=head.get(1).parent();
        Element temp2=temp1.nextElementSibling();
        Elements elements=temp2.select("a");
        System.out.print("hello");
        for (Element i :elements){
            linkArrayList.add(new Link(i.text(),i.attr("href")));
        }
        Toast.makeText(getApplicationContext(), "" + elements.size(), Toast.LENGTH_SHORT).show();

        google_adapter.notifyDataSetChanged();

        while (linkArrayList.size()==0){
            Thread.sleep(50);
        }
        check_connection=new Check_connection();
        check_connection.execute();
    }

    class Check_connection extends AsyncTask<Void,Void,Void> {
        ArrayList<Integer> accessable_url;
        ArrayList<Integer> not_accessable_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accessable_url=new ArrayList<Integer>();
            not_accessable_url=new ArrayList<Integer>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for(int i=0;i<3;i++){
                View view=yaunban_lv.getChildAt(i);
                view.setBackgroundColor(Color.BLUE);
            }
            Toast.makeText(getApplication(),"hello",Toast.LENGTH_SHORT).show();
            /**
             *
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i:accessable_url){
                        yaunban_lv.getChildAt(i).setBackgroundColor(Color.GREEN);
                        google_adapter.notifyDataSetChanged();
                    }
                    for (int i:not_accessable_url){
                        yaunban_lv.getChildAt(i).setBackgroundColor(Color.BLUE);
                        google_adapter.notifyDataSetChanged();
                    }

                    }
            });
             */
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i =0;i<linkArrayList.size();i++){
                final int finalI = i;
                OkHttpUtils.get()
                        .url(linkArrayList.get(i).getUrl())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                                not_accessable_url.add(finalI);
                            }

                            @Override
                            public void onResponse(String response) {
                                accessable_url.add(finalI);

                            }
                        });
            }
            return null;
        }
    }
}

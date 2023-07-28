package com.example.newsreader;
// NOTE YOU NEED TO DECLARE AN ARRAYLIST<NEWS> WHERE NEWS HAS A CONSTRUCTOR OF TITLE, DESC, DATE

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsRoom extends AppCompatActivity {

    ProgressBar progressBar;
    ListView listView;
    ArrayList <News> newsList;
    News currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_room);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        NewsQuery req = new NewsQuery();
        req.execute("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            News selectedItem = newsList.get(i);
            Uri uri = Uri.parse(selectedItem.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            News selectedItem = newsList.get(i);
            String pubDate = selectedItem.getPubDate();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewsRoom.this);
            alertDialogBuilder.setTitle("More Information" )
                    .setMessage("This article was published on: "  + '\n' + '\n' + pubDate  + '\n' + '\n' + "Do you want to add this article to your favourites?")
                    .setPositiveButton("Yes", (click, arg) -> {
                        try {
                            DbOpener dbOpener = new DbOpener(this);
                            SQLiteDatabase db = dbOpener.getWritableDatabase();
                            String title = selectedItem.getTitle();
                            String desc = selectedItem.getDescription();
                            String link = selectedItem.getLink();
                            ContentValues newFavourite = new ContentValues();
                            newFavourite.put(DbOpener.TITLE, title);
                            newFavourite.put(DbOpener.DESC, desc);
                            newFavourite.put(DbOpener.DATE, pubDate);
                            newFavourite.put(DbOpener.URL, link);
                            db.insert(DbOpener.TABLE_NAME, null, newFavourite);
                            db.close();
                            Log.d("Database", " Success" + title + desc  + pubDate + link);
                        } catch (Exception e) {
                            Log.d("Database", "Failed to save");
                        }
                    })
                    .setNegativeButton("No", (click, arg) -> {
                    })
            .create().show();
            return true;
        });
    }

    public class NewsQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                newsList = new ArrayList<>();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            currentItem = new News();
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")){
                            if (insideItem){
                                currentItem.setTitle(xpp.nextText());
                                publishProgress(25);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")){
                            if (insideItem){
                                currentItem.setLink(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")){
                            if (insideItem){
                                currentItem.setDescription(xpp.nextText());
                                publishProgress(50);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate")){
                            if (insideItem){
                                currentItem.setPubDate(xpp.nextText());
                                publishProgress(75);
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                        newsList.add(currentItem);
                    }
                    Log.d("Parser", String.valueOf(newsList));
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
                publishProgress(100);
            } catch (XmlPullParserException | IOException e) {
                Log.e("Error with Reader", e.getMessage());
            }
            return "done";
        }

        public void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            Log.i("HTTP", fromDoInBackground);
            ArrayAdapter<News> adapter = new ItemAdapter(NewsRoom.this, R.layout.row_layout, newsList);
            listView.setAdapter(adapter);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
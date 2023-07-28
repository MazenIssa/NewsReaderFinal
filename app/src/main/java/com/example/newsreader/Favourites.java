package com.example.newsreader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class Favourites extends AppCompatActivity {

    DbOpener dbOpener;
    SQLiteDatabase db;
    Cursor results;
    TextView title, desc;
    private MyListAdapter myAdapter;
    ArrayList<Favourite> favouritesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        ListView listView = findViewById(R.id.listView2);
        try {
            loadDataFromDatabase();
        } catch (Exception e) {
            Toast.makeText(this, "error loading DB", Toast.LENGTH_LONG).show();
        }
        try {
            printCursor();
        } catch (Exception e) {
            Toast.makeText(this, "error loading Cursor", Toast.LENGTH_LONG).show();
        }
        listView.setAdapter(myAdapter = new MyListAdapter());

        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            Favourite selectedItem = favouritesList.get(pos);
            String date = selectedItem.getDate();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("This article was published on: " + '\n' + date )
                    .setMessage("Do you want to delete this article?")
                        .setPositiveButton("Delete", (click, arg) -> {
                        deleteItem(selectedItem);
                        favouritesList.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Back to Favourites", (click, arg) ->{
                    })
                    .create().show();
            return true;
        });

        listView.setOnItemClickListener((p, b, pos, id) -> {
            Favourite selectedItem = favouritesList.get(pos);
            Uri uri = Uri.parse(selectedItem.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
    protected void deleteItem(Favourite x)
    {
        db.delete(DbOpener.TABLE_NAME, DbOpener.COL_ID + "= ?", new String[] {Long.toString(x.getId())});
    }

    private void loadDataFromDatabase() {
        dbOpener = new DbOpener(Favourites.this);
        db = dbOpener.getReadableDatabase();
        String [] columns = {DbOpener.COL_ID, DbOpener.TITLE, DbOpener.DESC, DbOpener.DATE, DbOpener.URL };
        results = db.query(false, DbOpener.TABLE_NAME, columns, null, null, null,
                null, null, null);
        favouritesList = new ArrayList<>();
        int colIndex = results.getColumnIndex(DbOpener.COL_ID);
        int titleIndex = results.getColumnIndex(DbOpener.TITLE);
        int descIndex = results.getColumnIndex(DbOpener.DESC);
        int dateIndex = results.getColumnIndex(DbOpener.DATE);
        int urlIndex = results.getColumnIndex(DbOpener.URL);
        while(results.moveToNext()){
            long id = results.getInt(colIndex);
            String title = results.getString(titleIndex);
            String desc = results.getString(descIndex);
            String date = results.getString(dateIndex);
            String url = results.getString(urlIndex);
            favouritesList.add(new Favourite(id, title, desc, date, url));
        }
    }


    private void printCursor() {
        int version = db.getVersion();
        int numColumns = results.getColumnCount();
        String columnNames = Arrays.toString(results.getColumnNames());
        int numResults = results.getCount();
        Log.v("PC ", "Database version " + version);
        Log.v("PC " ,"Column Count: " + numColumns);
        Log.v("PC ", "Column Names: " + columnNames);
        Log.v("PC ", "Number of Results: " + numResults);
        if (results.moveToFirst()) {
            do {
                StringBuilder sb = new StringBuilder();
                for (int idx=0; idx<numColumns; ++idx) {
                    sb.append(results.getString(idx));
                    if (idx < numColumns - 1)
                        sb.append("; ");
                }
                Log.v("PC ", String.format("Row: %d, Values: %s", results.getPosition(),
                        sb));
            } while (results.moveToNext());
        }
    }
    protected class MyListAdapter extends BaseAdapter {

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            Favourite favourite = favouritesList.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View newView;
            newView = inflater.inflate(R.layout.row_layout, null);
            title = newView.findViewById(R.id.title);
            title.setText(getItem(position).toString());
            title.setText(favourite.getTitle());
            desc = newView.findViewById(R.id.desc);
            desc.setText(getItem(position).toString());
            desc.setText(favourite.getDesc());
            return newView;
        }

        @Override
        public int getCount() {
            return favouritesList.size();
        }

        @Override
        public Object getItem(int position) {
            return favouritesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 1;
        }
    }


}
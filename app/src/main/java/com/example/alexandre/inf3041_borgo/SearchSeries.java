package com.example.alexandre.inf3041_borgo;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SearchSeries extends IntentService {
    private static final String ACTION_SEARCH_SERIES = "com.example.alexandre.inf30041_borgo.action.search_series";
    private static final String ACTION_SEARCH_NEXT = "com.example.alexandre.inf30041_borgo.action.search_next";
    private static final String ACTION_SEARCH_LAST = "com.example.alexandre.inf30041_borgo.action.search_LAST";

    private static final String EXTRA_TEXT = "com.example.alexandre.inf30041_borgo.extra.TEXT";
    private static final String EXTRA_ID = "com.example.alexandre.inf30041_borgo.extra.ID";

    public SearchSeries() {
        super("SearchSeries");
    }

    public static void startActionSearchSeries(Context context, String text) {
        Intent intent = new Intent(context, SearchSeries.class);
        intent.setAction(ACTION_SEARCH_SERIES);
        intent.putExtra(EXTRA_TEXT, text);
        context.startService(intent);
    }

    public static void startActionSearchNext(Context context, int id) {
        Intent intent = new Intent(context, SearchSeries.class);
        intent.setAction(ACTION_SEARCH_NEXT);
        intent.putExtra(EXTRA_ID, id);
        context.startService(intent);
    }

    public static void startActionSearchLast(Context context, int id) {
        Intent intent = new Intent(context, SearchSeries.class);
        intent.setAction(ACTION_SEARCH_LAST);
        intent.putExtra(EXTRA_ID, id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEARCH_SERIES.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_TEXT);
                handleActionSeriesUpdate(text);
            } else
            if (ACTION_SEARCH_NEXT.equals(action)) {
                final int id = intent.getIntExtra(EXTRA_ID, 0);
                handleActionSeriesNext(id);
            } else if (ACTION_SEARCH_LAST.equals(action)) {
                final int id = intent.getIntExtra(EXTRA_ID, 0);
                handleActionSeriesLast(id);
            }
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;

            while((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleActionSeriesUpdate(String text) {
        URL url = null;
        try {
            url = new URL("https://api.betaseries.com/shows/search?v=2.4&key=f87721eb41e8&title=" + text + "&order=popularity&nbpp=50");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(), "series.json"));
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SearchFragment.SERIES_UPDATE));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleActionSeriesNext(int id) {
        URL url = null;
        try {
            url = new URL("https://api.betaseries.com/episodes/next?v=2.4&key=f87721eb41e8&id=" + id);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(), "next.json"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SearchFragment.SERIES_NEXT));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleActionSeriesLast(int id) {
        URL url = null;
        try {
            url = new URL("https://api.betaseries.com/episodes/latest?v=2.4&key=f87721eb41e8&id=" + id);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(), "last.json"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SearchFragment.SERIES_LAST));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

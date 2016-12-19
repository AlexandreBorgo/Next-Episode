package com.example.alexandre.inf3041_borgo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Alexandre on 19/12/2016.
 */

public class AlarmReceiver extends IntentService {
    public AlarmReceiver(String name) {
        super(name);
    }

    public AlarmReceiver() {
        super("AlarmReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        BaseHelper baseHelper = new BaseHelper(this);
        SQLiteDatabase series = baseHelper.getWritableDatabase();

        String[] projection = {
                BaseHelper.SERIES_TITLE,
                BaseHelper.SERIES_IMAGE,
                BaseHelper.SERIES_NEXT
        };

        String selection = BaseHelper.SERIES_NEXT + " = ? OR " + BaseHelper.SERIES_NEXT  + " = ?";

        GregorianCalendar calendar = new GregorianCalendar();

        String[] selectionArgs = {
                calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH) + "-" + (calendar.get(Calendar.DAY_OF_MONTH)-1)
        };

        Cursor cursor = series.query(BaseHelper.SERIES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notif2 = prefs.getBoolean("notif_day", false);
        boolean notif3 = prefs.getBoolean("notif_dayafter", false);

        if(cursor.getCount() > 0) {

            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int notifId = 400 + i;

                String date = cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_NEXT));
                String[] dateFormat = date.split("-");
                GregorianCalendar timeEpisode = new GregorianCalendar(Integer.valueOf(dateFormat[0]), Integer.valueOf(dateFormat[1]), Integer.valueOf(dateFormat[2]));

                if(timeEpisode.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && timeEpisode.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    if(notif2) {
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                        notificationBuilder.setContentTitle(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_TITLE)));
                        notificationBuilder.setContentText(getString(R.string.episode_today));
                        Notification notification = notificationBuilder.build();
                        notificationManager.notify(notifId, notification);
                    }
                }
                else {
                    if(notif3) {
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                        notificationBuilder.setContentTitle(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_TITLE)));
                        notificationBuilder.setContentText(getString(R.string.episode_yesterday));
                        Notification notification = notificationBuilder.build();
                        notificationManager.notify(notifId, notification);
                    }
                }
            }
        }
        else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setContentText(getString(R.string.no_episode_today));
            notificationManager.notify(1, notificationBuilder.build());
        }

        String[] projection2 = {
                BaseHelper.SERIES_ID
        };

        String selection2 = BaseHelper.SERIES_NEXT + " = ? OR " + BaseHelper.SERIES_NEXT  + " = ? OR " + BaseHelper.SERIES_NEXT  + " = ?";

        GregorianCalendar calendar2 = new GregorianCalendar();

        String[] selectionArgs2 = {
                calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH) + "-" + (calendar.get(Calendar.DAY_OF_MONTH)-2),
                "2050-12-31"
        };

        Cursor cursor2 = series.query(BaseHelper.SERIES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if(cursor2.getCount() > 0) {
            for(int i = 0; i < cursor2.getCount(); i++) {
                cursor2.moveToPosition(i);
                SearchSeries.startActionSearchNext(this, Integer.valueOf(cursor2.getString(cursor2.getColumnIndexOrThrow(BaseHelper.SERIES_ID))));
                SearchSeries.startActionSearchLast(this, Integer.valueOf(cursor2.getString(cursor2.getColumnIndexOrThrow(BaseHelper.SERIES_ID))));
            }
        }

        series.close();
        cursor.close();
        cursor2.close();
    }
}

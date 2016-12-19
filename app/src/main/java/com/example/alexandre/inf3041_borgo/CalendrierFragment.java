package com.example.alexandre.inf3041_borgo;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendrierFragment extends Fragment {


    public CalendrierFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendrier, container, false);

        BaseHelper baseHelper = new BaseHelper(getContext());
        SQLiteDatabase series = baseHelper.getWritableDatabase();

        String[] projection = {
                BaseHelper.SERIES_ID,
                BaseHelper.SERIES_TITLE,
                BaseHelper.SERIES_IMAGE,
                BaseHelper.SERIES_BANNER,
                BaseHelper.SERIES_LAST,
                BaseHelper.SERIES_NEXT
        };

        String selection = BaseHelper.SERIES_NEXT + " >= ?";

        GregorianCalendar calendar = new GregorianCalendar();

        String[] selectionArgs = {
                calendar.get(Calendar.YEAR)+ "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
        };

        String sortOrder = BaseHelper.SERIES_NEXT;

        Cursor cursor = series.query(BaseHelper.SERIES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_see_episode);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new CalendrierFragment.SeriesAdapter(cursor));

        if(cursor.getCount() <= 0) {
            Toast.makeText(getContext(), R.string.empty_message, Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public class SeriesAdapter extends RecyclerView.Adapter<CalendrierFragment.SeriesAdapter.SeriesHolder> {

        private Cursor cursor = null;

        SeriesAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public CalendrierFragment.SeriesAdapter.SeriesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_episode, parent, false);
            return new CalendrierFragment.SeriesAdapter.SeriesHolder(view);
        }

        @Override
        public void onBindViewHolder(CalendrierFragment.SeriesAdapter.SeriesHolder holder, int position) {
            cursor.moveToPosition(position);
            Picasso.with(getContext()).load(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_IMAGE))).into(holder.image);
            holder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_TITLE)));
            holder.date.setText(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_NEXT)));

            if(holder.date.getText().equals("2050-12-31")) {
                holder.date.setText(R.string.no_date);
            }
            else {
                String date = holder.date.getText().toString();
                String[] dateFormat = date.split("-");

                GregorianCalendar timeEpisode = new GregorianCalendar(Integer.valueOf(dateFormat[0]), Integer.valueOf(dateFormat[1]), Integer.valueOf(dateFormat[2]));

                String day = "";

                switch(timeEpisode.get(Calendar.DAY_OF_WEEK)) {
                    case 1:
                        day = getString(R.string.sunday);
                        break;
                    case 2:
                        day = getString(R.string.monday);
                        break;
                    case 3:
                        day = getString(R.string.tuesday);
                        break;
                    case 4:
                        day = getString(R.string.wednesday);
                        break;
                    case 5:
                        day = getString(R.string.thursday);
                        break;
                    case 6:
                        day = getString(R.string.friday);
                        break;
                    case 7:
                        day = getString(R.string.saturday);
                        break;
                }

                holder.date.setText(day + " " + timeEpisode.get(Calendar.DAY_OF_MONTH) + "/" + timeEpisode.get(Calendar.MONTH) + "/" + timeEpisode.get(Calendar.YEAR));
            }

            holder.id = cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_ID));
            holder.banner = cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_BANNER));
            holder.next = cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_NEXT));
            holder.last = cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_LAST));
        }

        @Override
        public int getItemCount() {
            return this.cursor.getCount();
        }

        public void setNewSeriesList(Cursor cursor) {
            this.cursor = cursor;
            this.notifyDataSetChanged();
        }

        public class SeriesHolder extends RecyclerView.ViewHolder {

            public TextView name = null;
            public TextView date = null;
            public String next = null;
            public String last = null;
            public String id = null;
            public ImageView image = null;
            public String banner = null;

            public SeriesHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.rv_series_title);
                date = (TextView) itemView.findViewById(R.id.rv_series_date);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }
}

package com.example.alexandre.inf3041_borgo;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, View.OnKeyListener {

    public SearchFragment() {
        // Required empty public constructor
    }

    public static final String SERIES_UPDATE = "com.example.alexandre.inf30041_borgo.SERIES_UPDATE";
    public static final String SERIES_NEXT = "com.example.alexandre.inf30041_borgo.SERIES_NEXT";
    public static final String SERIES_LAST = "com.example.alexandre.inf30041_borgo.SERIES_LAST";
    private RecyclerView recyclerView = null;

    public class SeriesUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                InputStream inputStream = new FileInputStream(context.getCacheDir() + "/series.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                String result = new String(buffer, "UTF-8");

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("shows");

                //Log.i("Result", jsonArray.toString());

                SeriesAdapter seriesAdapter = (SeriesAdapter) recyclerView.getAdapter();
                seriesAdapter.setNewSeries(jsonArray);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class SeriesNext extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                InputStream inputStream = new FileInputStream(context.getCacheDir() + "/next.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                String result = new String(buffer, "UTF-8");

                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("episode");

                if(!jsonObject1.isNull("id")) {
                    BaseHelper baseHelper = new BaseHelper(context);
                    SQLiteDatabase series = baseHelper.getWritableDatabase();
                    GregorianCalendar calendar = new GregorianCalendar();
                    ContentValues values = new ContentValues();
                    values.put(BaseHelper.SERIES_NEXT, jsonObject1.getString("date"));
                    values.put(BaseHelper.SERIES_NEXTID, jsonObject1.getString("id"));

                    String selection = BaseHelper.SERIES_ID + " = ?";
                    String[] selectionArgs = { jsonObject1.getJSONObject("show").getString("id") };

                    series.update(BaseHelper.SERIES_TABLE_NAME, values, selection, selectionArgs);

                    baseHelper.close();
                    series.close();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class SeriesLast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                InputStream inputStream = new FileInputStream(context.getCacheDir() + "/last.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                String result = new String(buffer, "UTF-8");

                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("episode");

                if(!jsonObject1.isNull("id")) {
                    BaseHelper baseHelper = new BaseHelper(context);
                    SQLiteDatabase series = baseHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(BaseHelper.SERIES_LAST, jsonObject1.getString("date"));
                    values.put(BaseHelper.SERIES_LASTID, jsonObject1.getString("id"));

                    String selection = BaseHelper.SERIES_ID + " = ?";
                    String[] selectionArgs = { jsonObject1.getJSONObject("show").getString("id") };

                    series.update(BaseHelper.SERIES_TABLE_NAME, values, selection, selectionArgs);

                    baseHelper.close();
                    series.close();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton search = (ImageButton) view.findViewById(R.id.searchButton);
        search.setOnClickListener(this);

        EditText editText = (EditText) view.findViewById(R.id.edit);
        editText.setOnKeyListener(this);

        IntentFilter intentFilter = new IntentFilter(SERIES_UPDATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new SeriesUpdate(),intentFilter);

        IntentFilter intentFilter2 = new IntentFilter(SERIES_NEXT);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new SeriesNext(),intentFilter2);

        IntentFilter intentFilter3 = new IntentFilter(SERIES_LAST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new SeriesLast(),intentFilter3);

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_series);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SeriesAdapter(null));

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.searchButton:
                search(view);
                break;
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (i)
            {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    search(view);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    public void search(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        EditText editText = (EditText) getView().findViewById(R.id.edit);
        if(editText.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.error_input_series, Toast.LENGTH_LONG).show();
        }
        else {
            SearchSeries.startActionSearchSeries(getContext(), editText.getText().toString());
        }
    }

    public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesHolder> {

        private JSONArray series = null;

        SeriesAdapter(JSONArray series) {
            this.series = series;
        }

        @Override
        public SeriesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_series, parent, false);
            return new SeriesHolder(view);
        }

        @Override
        public void onBindViewHolder(SeriesHolder holder, int position) {
            if(this.series != null) {
                try {
                    JSONObject jsonObject = this.series.getJSONObject(position);
                    holder.name.setText(jsonObject.getString("title"));
                    holder.date.setText(jsonObject.getString("creation"));
                    holder.id = jsonObject.getString("id");
                    holder.description = jsonObject.getString("description");
                    holder.banner = "https://www.betaseries.com" + jsonObject.getJSONObject("images").getString("banner");
                    holder.imageUrl = "https://www.betaseries.com" + jsonObject.getJSONObject("images").getString("poster");

                    String urlImage = "https://www.betaseries.com" + jsonObject.getJSONObject("images").getString("poster");
                    Picasso.with(getContext()).load(urlImage).into(holder.image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount() {
            if(this.series != null) {
                //Log.i("length", "" + this.series.length());
                return this.series.length();
            }
            else return 0;
        }

        public void setNewSeries(JSONArray jsonArray) {
            this.series = jsonArray;
            this.notifyDataSetChanged();
        }

        public class SeriesHolder extends RecyclerView.ViewHolder {

            public TextView name = null;
            public TextView date = null;
            public String id = null;
            public String description = null;
            public String banner = null;
            public String imageUrl = null;
            public ImageView image = null;

            public void add() {
                BaseHelper baseHelper = new BaseHelper(getContext());
                SQLiteDatabase series = baseHelper.getWritableDatabase();

                String[] projection = {
                        BaseHelper.SERIES_ID,
                        BaseHelper.SERIES_TITLE,
                        BaseHelper.SERIES_IMAGE
                };

                String selection = BaseHelper.SERIES_ID + " = ?";
                String[] selectionArgs = { id };

                Cursor cursor = series.query(BaseHelper.SERIES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);

                if(cursor.getCount() < 1) {

                    ContentValues values = new ContentValues();
                    values.put(BaseHelper.SERIES_ID, id);
                    values.put(BaseHelper.SERIES_TITLE, name.getText().toString());
                    values.put(BaseHelper.SERIES_LAST, "unknown");
                    values.put(BaseHelper.SERIES_NEXT, "2050-12-31");
                    values.put(BaseHelper.SERIES_LASTID, 0);
                    values.put(BaseHelper.SERIES_NEXTID, 0);
                    values.put(BaseHelper.SERIES_IMAGE, imageUrl);
                    values.put(BaseHelper.SERIES_BANNER, banner);
                    values.put(BaseHelper.SERIES_SEEN, 0);

                    long newRowId = series.insert(BaseHelper.SERIES_TABLE_NAME, null, values);

                    if (newRowId != -1) {
                        Toast.makeText(getContext(), name.getText() + getString(R.string.add_serie_done), Toast.LENGTH_LONG).show();
                        SearchSeries.startActionSearchNext(getContext(), Integer.valueOf(id));
                        SearchSeries.startActionSearchLast(getContext(), Integer.valueOf(id));
                    } else {
                        Toast.makeText(getContext(), R.string.errorInsert, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), R.string.following, Toast.LENGTH_LONG).show();
                }
            }

            public SeriesHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.rv_series_title);
                date = (TextView) itemView.findViewById(R.id.rv_series_date);
                image = (ImageView) itemView.findViewById(R.id.image);
                Button add = (Button) itemView.findViewById(R.id.rv_series_add);
                Button see = (Button) itemView.findViewById(R.id.rv_series_see);

                see.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View viewDialog = inflater.inflate(R.layout.dialog_see, null);
                        builder.setView(viewDialog);
                        ImageView imageView = (ImageView) viewDialog.findViewById(R.id.banner);
                        ((TextView)viewDialog.findViewById(R.id.title)).setText(name.getText());
                        ((TextView)viewDialog.findViewById(R.id.date)).setText(date.getText());
                        ((TextView)viewDialog.findViewById(R.id.description)).setText(description);

                        Picasso.with(getContext()).load(banner).into(imageView);

                        final AlertDialog dialog = builder.create();

                        ((Button)viewDialog.findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                add();
                            }
                        });


                        ((Button)viewDialog.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add();
                    }
                });
            }
        }
    }
}

package com.example.alexandre.inf3041_borgo;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesFragment extends Fragment {


    public SeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_series, container, false);

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

        Cursor cursor = series.query(BaseHelper.SERIES_TABLE_NAME, projection, null, null, null, null, null);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_see_series);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SeriesFragment.SeriesAdapter(cursor));

        if(cursor.getCount() <= 0) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.empty_list);
            builder.setMessage(R.string.empty_message);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();*/

            Toast.makeText(getContext(), R.string.empty_message, Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesHolder> {

        private Cursor cursor = null;

        SeriesAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public SeriesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_see, parent, false);
            return new SeriesHolder(view);
        }

        @Override
        public void onBindViewHolder(SeriesHolder holder, int position) {
            cursor.moveToPosition(position);
            Picasso.with(getContext()).load(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_IMAGE))).into(holder.image);
            holder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(BaseHelper.SERIES_TITLE)));
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
            public String next = null;
            public String last = null;
            public String id = null;
            public ImageView image = null;
            public String banner = null;

            public SeriesHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.rv_series_title);
                image = (ImageView) itemView.findViewById(R.id.image);
                Button see = (Button) itemView.findViewById(R.id.rv_series_see);
                Button unfollow = (Button) itemView.findViewById(R.id.rv_series_unfollow);

                see.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View viewDialog = inflater.inflate(R.layout.dialog_see2, null);
                        builder.setView(viewDialog);
                        ImageView imageView = (ImageView) viewDialog.findViewById(R.id.banner);
                        ((TextView)viewDialog.findViewById(R.id.title)).setText(name.getText());
                        ((TextView)viewDialog.findViewById(R.id.next)).setText(getString(R.string.next) + " " + next);
                        ((TextView)viewDialog.findViewById(R.id.last)).setText(getString(R.string.latest) + " " + last);

                        Picasso.with(getContext()).load(banner).into(imageView);

                        final AlertDialog dialog = builder.create();

                        ((Button)viewDialog.findViewById(R.id.unfollowButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                remove();
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

                unfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove();
                    }
                });
            }

            public void remove() {
                BaseHelper baseHelper = new BaseHelper(getContext());
                SQLiteDatabase series = baseHelper.getWritableDatabase();

                String selection = BaseHelper.SERIES_ID + " = ?";
                String[] selectionArgs = { id };

                series.delete(BaseHelper.SERIES_TABLE_NAME, selection, selectionArgs);

                Toast.makeText(getContext(), name.getText() + getString(R.string.removed), Toast.LENGTH_LONG).show();

                baseHelper.close();
                series.close();

                BaseHelper baseHelper2 = new BaseHelper(getContext());
                SQLiteDatabase series2 = baseHelper.getWritableDatabase();

                String[] projection = {
                        BaseHelper.SERIES_ID,
                        BaseHelper.SERIES_TITLE,
                        BaseHelper.SERIES_IMAGE,
                        BaseHelper.SERIES_BANNER,
                        BaseHelper.SERIES_LAST,
                        BaseHelper.SERIES_NEXT
                };

                Cursor cursor = series2.query(BaseHelper.SERIES_TABLE_NAME, projection, null, null, null, null, null);
                setNewSeriesList(cursor);
            }
        }
    }
}

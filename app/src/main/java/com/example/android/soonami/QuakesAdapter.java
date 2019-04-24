package com.example.android.soonami;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuakesAdapter extends RecyclerView.Adapter<QuakesAdapter.MyViewHolder> {

    private static List<Event> eventsList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time, alert;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.date);
            alert = view.findViewById(R.id.tsunami_alert);
        }
    }

    public QuakesAdapter(Context context, List<Event> eventsList) {
        this.eventsList = eventsList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event events = eventsList.get(position);
        holder.title.setText(events.getTitle());
        holder.time.setText(formatTime(events.getTime()));
        holder.alert.setText(""+events.getTsunamiAlert());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public String formatTime(Long time){
        Date dateObject = new Date(time);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, y");
        String dateToDisplay = dateFormatter.format(dateObject);
        return dateToDisplay;
    }

}

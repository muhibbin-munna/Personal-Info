package com.indian.youthcareerinfo.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.model.UploadNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<UploadNotification> mUploads;

    public NotificationAdapter(Context mContext, List<UploadNotification> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_notification_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {

        UploadNotification uploadCurrent = mUploads.get(position);

        holder.titleTextView.setText(String.valueOf(uploadCurrent.getTitle()));
        holder.bodyTextView.setText(String.valueOf(uploadCurrent.getBody()));
        holder.dateTextView.setText(DateFormat.format("dd/MM/yyyy hh:mm:ss", Long.parseLong(String.valueOf(uploadCurrent.getTime()))).toString());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView,bodyTextView,dateTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_layout_id);
            bodyTextView = itemView.findViewById(R.id.body_layout_id);
            dateTextView = itemView.findViewById(R.id.date_layout_id);

        }
    }
}

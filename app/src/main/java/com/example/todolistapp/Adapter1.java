package com.example.todolistapp;

import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.LogRecord;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class Adapter1 extends RecyclerView.Adapter<Adapter1.ViewHolder> {

    private ArrayList<Model> modelA;
    private Database database;
    private Context context;

    Adapter1 itemsAdapter;
    private RecyclerView recyclerView;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;


    public Adapter1(ArrayList<Model> modelA, Database database, Context context) {
        this.modelA = modelA;
        this.database = database;
        this.context = context;


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItem;
        public Button delete;

        public CardView card;

        public TextView textview1;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.textviewdesign);
            delete = (Button) itemView.findViewById(R.id.delete);
            card = (CardView) itemView.findViewById(R.id.card);
            textview1 = (TextView) itemView.findViewById(R.id.textview1);


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.designfile, parent, false);


        return new ViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull Adapter1.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvItem.setText(String.valueOf(modelA.get(position).getName()));
        holder.textview1.setText(String.valueOf(modelA.get(position).getTime()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionToDelete = holder.getBindingAdapterPosition();
                Cursor cursor = database.viewData();
                if (cursor.moveToPosition(positionToDelete)) {
                    @SuppressLint("Range") int itemId = cursor.getInt(cursor.getColumnIndex(Database.KEY_ID));
                    modelA.remove(positionToDelete);
                    notifyItemRemoved(positionToDelete);

                    notifyItemRangeChanged(positionToDelete, getItemCount());
                    database.deleteItem(itemId);
                }
                cursor.close();
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(holder.itemView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update the Text View with the selected Time
                        String selectedTime = hourOfDay + ":" + minute;
                        holder.textview1.setText(selectedTime);
                        modelA.get(position).setTime(selectedTime);

                        int positionToUpdate = holder.getBindingAdapterPosition();
                        Cursor cursor = database.viewData();
                        if (cursor.moveToPosition(positionToUpdate)) {
                            @SuppressLint("Range") int itemId = cursor.getInt(cursor.getColumnIndex(Database.KEY_ID));
                            database.updateTime(itemId, selectedTime);

                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    LocalTime enteredTime = LocalTime.of(hourOfDay, minute);
                                    LocalTime currentTime = LocalTime.now();

                                    if (currentTime.compareTo(enteredTime) >= 0) {
                                        showNotification(modelA.get(position).getName().toString());
                                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        notificationManagerCompat.notify(1, notification);


                                    } else {
                                        handler.postDelayed(this, 1000);
                                    }
                                }
                            };
                            handler.post(runnable);


                        }

                        cursor.close();


                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return modelA.size();
    }


    //    private void showPopupDialog(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Time Alert").setMessage("You have to do : "+message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false).create().show();
//    }
    private void showNotification(String Message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channelId", " ", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(context, NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelId").setSmallIcon(R.drawable.done_button).setContentText("You have to do " + Message);
        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(context);


        //}

        // Set the target device's FCM token


    }
}



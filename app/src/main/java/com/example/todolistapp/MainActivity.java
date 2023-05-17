package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<Model> modelA;
    Adapter1 itemsAdapter;

    RecyclerView recyclerView;
    EditText editText;
    private ImageView imageView;
    private Database database;
    private Context context;
    private Button button;
    private TextView textview1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the Layout Inflator
        View view = getLayoutInflater().inflate(R.layout.designfile, null);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.clearbutton);
        textview1 = view.findViewById(R.id.textview1);
        // Initialize database
        database = new Database(this);
        context = this;
        modelA = new ArrayList<>();
        // Initialize shared preferences


        // Initialize items list and adapter

        itemsAdapter = new Adapter1(modelA, database, context);

        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = editText.getText().toString();
                String time = "Set Time";
                if (newItem.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please write some task", Toast.LENGTH_SHORT).show();
                } else {
                    itemsAdapter.notifyItemInserted(modelA.size() + 1);
                    database.addItem(newItem, time);
                    modelA.add(new Model(newItem, time));

                    itemsAdapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });

        // Load items from database
        loadItemsFromDatabase();

        // Set the value of textview1 from shared preferences
//        String textview1Value = sharedPreferences.getString(TEXTVIEW1_VALUE, "");
//        textview1.setText(textview1Value);

        // Clearing the whole to do list app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Your list has been cleared", Toast.LENGTH_SHORT).show();
                database.deleteAlldata();
                modelA.clear();
                itemsAdapter.notifyDataSetChanged();

                textview1.setText("");
            }
        });
    }

    private void loadItemsFromDatabase() {
        Cursor cursor = database.viewData();
        if (cursor.moveToFirst()) {
            do {
                modelA.add(new Model(cursor.getString(1), cursor.getString(2)));

            } while (cursor.moveToNext());
        }
        cursor.close();
    }


}



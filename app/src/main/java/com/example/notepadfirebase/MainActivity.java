package com.example.notepadfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etId, etName, etEmail;
    TextView tvResult;
    Button btnInsert, btnRead, btnUpdate, btnDelete;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        tvResult = findViewById(R.id.tvResult);

        btnInsert = findViewById(R.id.btnInsert);
        btnRead = findViewById(R.id.btnRead);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Tasks");

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTask();
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readTasks();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });


    }

    private void insertTask() {
        String id = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.notepadfirebase.Task task = new com.example.notepadfirebase.Task(id, name, email);

        databaseReference.child(id).setValue(task)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void readTasks() {
        tvResult.setText("");

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StringBuilder sb = new StringBuilder();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            com.example.notepadfirebase.Task task = data.getValue(com.example.notepadfirebase.Task.class);
                            if (task != null) {
                                sb.append("ID: ").append(task.getId()).append("\n");
                                sb.append("Task: ").append(task.getName()).append("\n");
                                sb.append("Description: ").append(task.getEmail()).append("\n\n");
                            }
                        }
                        tvResult.setText(sb.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTask() {
        insertTask(); // overwrite existing data
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private void deleteTask() {
        String id = etId.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Enter ID", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(id).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void clearFields() {
        etId.setText("");
        etName.setText("");
        etEmail.setText("");
    }
}
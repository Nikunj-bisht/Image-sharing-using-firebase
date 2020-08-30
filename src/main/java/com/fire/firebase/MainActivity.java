package com.fire.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    private EditText editText1;

    private EditText editText2;
    private EditText editText3;
    private Button button1;
    private Button button2;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.email);

        editText2 = findViewById(R.id.name);
        editText3 = findViewById(R.id.password);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);


        firebaseAuth=FirebaseAuth.getInstance();


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseApp.initializeApp(MainActivity.this);


                firebaseAuth.createUserWithEmailAndPassword(editText1.getText().toString(),editText3.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseDatabase.getInstance().getReference()
                                    .child("may_users")
                                    .child(task.getResult().getUser().getUid())
                                    .child("username").setValue(editText2.getText().toString());

                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editText2.getText().toString())
                                    .build();

                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                            Intent intent = new Intent(MainActivity.this,Newactivity.class);
                            startActivity(intent);
                        }else{

                        }
                    }
                });
            }
        });
button2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        firebaseAuth.signInWithEmailAndPassword(editText1.getText().toString(),editText3.getText().toString()).addOnCompleteListener( MainActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(MainActivity.this, "Welcome "+task.getResult().getUser().getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this,Newactivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "Sorry ", Toast.LENGTH_LONG).show();

                }
            }
        });


    }
});

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

if(currentUser!=null){

}
    }

}
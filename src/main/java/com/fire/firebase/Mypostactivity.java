package com.fire.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class Mypostactivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,AdapterView.OnItemLongClickListener{

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private ImageView imageView;
    private ArrayList<DataSnapshot> dataSnapshots;
private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypostactivity);
this.listView = findViewById(R.id.friendlist);
listView.setOnItemClickListener(Mypostactivity.this);
listView.setOnItemLongClickListener(Mypostactivity.this);
        this.arrayList = new ArrayList<>();
        this.arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
this.listView.setAdapter(arrayAdapter);
this.imageView = findViewById(R.id.friendimag);
dataSnapshots=new ArrayList<>();
textView = findViewById(R.id.textView3);

        FirebaseDatabase.getInstance().getReference()
                .child("may_users")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid()).child("receivedimage")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        dataSnapshots.add(snapshot);
                        String username = String.valueOf(snapshot.child("from").getValue());
                        arrayList.add(username);
                        arrayAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
int i=0;
                        for(DataSnapshot snapshot1 : dataSnapshots){

                            if(snapshot1.getValue().equals(snapshot.getKey())){
                                dataSnapshots.remove(i);
                                arrayList.remove(i);
                            }
                            i++;
                        }
                        arrayAdapter.notifyDataSetChanged();
                       // imageView.setImageResource(R.drawable.googleg_standard_color_18);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        final DataSnapshot mydata = dataSnapshots.get(i);
        String url = (String)mydata.child("imagelink").getValue();
        RequestQueue requestQueue = Volley.newRequestQueue(Mypostactivity.this);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading image send By.."+mydata.child("from").getValue());
        progressDialog.show();

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
                progressDialog.dismiss();
                textView.setText(mydata.child("caption").getValue().toString());

            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP,Bitmap.Config.RGB_565 ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(imageRequest);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Mypostactivity.this);
        builder.setTitle("Delete this post..");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int s) {



                FirebaseDatabase.getInstance().getReference().child("may_users")
                        .child(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid()).child("receivedimage").child(dataSnapshots.get(i).getKey()).removeValue();

                FirebaseStorage.getInstance().getReference().child("Images").child((String)dataSnapshots.get(i).child("imageidenti").getValue()).delete();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

     return true;
    }
}
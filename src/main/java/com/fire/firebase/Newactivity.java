package com.fire.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.UUID;

public class Newactivity extends AppCompatActivity {

    private Button button;
private ImageView imageView;
    private StorageReference mStorageRef;
    public static String imageidenti;
    private Button button2;
  private   Bitmap bitmap;
  private EditText editText;
     ListView listView;
    ArrayList<String> arrayList;
    Adaptor adaptor;
   ArrayList<String> arraylist2;
public static String downloadlink;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==200 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            selectimage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newactivity);




    button = findViewById(R.id.button3);
imageView=findViewById(R.id.imageView);
button2=findViewById(R.id.upload);
editText=findViewById(R.id.caption);
mStorageRef= FirebaseStorage.getInstance().getReference();
listView = findViewById(R.id.list);

    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            selectimage();

        }
    });
button2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

if(bitmap!=null) {
    uploadimagetoserver(bitmap);
}else{
    Snackbar.make(view,"Please select image", BaseTransientBottomBar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectimage();
        }
    }).show();
}
    }
});
this.arrayList=new ArrayList<>();
        this.arraylist2 = new ArrayList<>();

adaptor = new Adaptor(Newactivity.this,arrayList,editText,arraylist2);

listView.setAdapter(adaptor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myposts,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.post){
            Intent intent = new Intent(Newactivity.this,Mypostactivity.class);
            startActivity(intent);


        }


        return super.onOptionsItemSelected(item);
    }

    public void selectimage(){

        if(ActivityCompat.checkSelfPermission(Newactivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(Newactivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);


        }else{

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent,100);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && resultCode==RESULT_OK && data!=null){
            Uri uri=data.getData();
            try {
                this.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);

                imageView.setImageBitmap(bitmap);
                editText.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public void uploadimagetoserver(Bitmap bitmap){
        // Get the data from an ImageView as bytes
       imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading..");
        progressDialog.show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
imageidenti= UUID.randomUUID() + ".jpeg";
        UploadTask uploadTask = mStorageRef.child("Images").child(imageidenti).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
progressDialog.dismiss();

                FirebaseDatabase.getInstance().getReference().child("may_users").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        arraylist2.add(snapshot.getKey());
                        String name = String.valueOf(snapshot.child("username").getValue());
arrayList.add(name);
adaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
    @Override
    public void onComplete(@NonNull Task<Uri> task) {
        if(task.isSuccessful()){
            downloadlink = task.getResult().toString();
        }
    }
});
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }
}
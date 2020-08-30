package com.fire.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Adaptor extends BaseAdapter {
  public  Context context;
   public LayoutInflater layoutInflater;
    public ArrayList<String> arrayList;
      public EditText editText;
        public ArrayList<String> getArrayList;
    public Adaptor(Context context,ArrayList<String> arrayList,EditText editText,ArrayList<String> arrayList1){
        this.context = context;
        this.arrayList = arrayList;
        this.layoutInflater = LayoutInflater.from(context);
        this.editText = editText;
        this.getArrayList = arrayList1;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
final int p=i;
        view = this.layoutInflater.inflate(R.layout.users,null);
TextView text=view.findViewById(R.id.textView);
        ImageView view1= view.findViewById(R.id.imageView2);
        view1.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
text.setText(arrayList.get(i));
view.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        HashMap<String,String> map = new HashMap<>();

        map.put("from", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        map.put("imageidentifier",Newactivity.imageidenti);
        map.put("imagelink",Newactivity.downloadlink);
        map.put("caption",editText.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("may_users").child(getArrayList.get(i)).child("receivedimage").push().setValue(map);

    }
});


        return view;
    }
}

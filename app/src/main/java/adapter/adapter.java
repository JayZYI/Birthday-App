package adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birtdayapp2.EditActivity;
import com.example.birtdayapp2.R;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import helper.dbHelper;
import helper.modelHelper;

public class adapter extends RecyclerView.Adapter<adapter.ContactViewHolder>
{
    private ArrayList<modelHelper> contactArrayList = new ArrayList<>();
    private Activity activity;
    private helper.dbHelper dbHelper;

    private Calendar calendar, newCalender;
    private SimpleDateFormat dateFormat;
    private String date;

    public adapter(Activity activity)
    {
        this.activity = activity;
        dbHelper = new dbHelper(activity);
    }

    public ArrayList<modelHelper> getContactArrayList() {
        return contactArrayList;
    }

    public void setContactArrayList(ArrayList<modelHelper> contactArrayList)
    {
        if(contactArrayList.size() > 0)
        {
            this.contactArrayList.clear();
        }

        this.contactArrayList.addAll(contactArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bapp, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position)
    {
        String Birth_date = contactArrayList.get(position).getBirth_date();
        byte[] bytes = contactArrayList.get(position).getImage();

        if(bytes != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.tvName.setText(contactArrayList.get(position).getName());
        holder.tvPhone.setText(Birth_date);

        holder.layout.setOnClickListener((View view) -> {
            Intent intent = new Intent(activity, EditActivity.class);
            intent.putExtra("contact", (Serializable) contactArrayList.get(position));
            activity.startActivity(intent);
        });

        //test
        calendar = Calendar.getInstance();
        String dateFromDb = contactArrayList.get(position).getBirth_date();
        System.out.println(dateFromDb);


        dateFormat = new SimpleDateFormat("dd/MM");
        date = dateFormat.format(calendar.getTime());
        System.out.println(date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        Date date1, date2;
        try {
            date1 = sdf.parse(dateFromDb);
            date2 = sdf.parse(date);

            if(date1.compareTo(date2) == 0){
                holder.tvHidden.setText("Today birthday!");
                holder.layout.setBackgroundColor(Color.rgb(0, 255,0));
            }else{
                System.out.println("tidak sama");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



//        holder.btnCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Dexter.withContext(activity)
//                        .withPermission(Manifest.permission.CALL_PHONE)
//                        .withListener(new PermissionListener() {
//                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
//                                makeCall(phone_number);
//                            }
//                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
//                                showSettingsDialog();
//                            }
//                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                                token.continuePermissionRequest();
//                            }
//                        }).check();
//            }
//        });

//        holder.btnMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Dexter.withContext(activity)
//                        .withPermission(Manifest.permission.SEND_SMS)
//                        .withListener(new PermissionListener() {
//                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
//                                sendMessage(phone_number);
//                            }
//                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
//                                showSettingsDialog();
//                            }
//                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                                token.continuePermissionRequest();
//                            }
//                        }).check();
//
//            }
//        });
    }

    public void sendMessage(String phone_number)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone_number));
        activity.startActivity(intent);
    }

    public void makeCall(String phone_number)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone_number));
        activity.startActivity(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName, tvPhone,tvHidden;
        RelativeLayout layout;
        Button btnCall, btnMsg;
        ImageView imageView;

        public ContactViewHolder(View view)
        {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvPhone = view.findViewById(R.id.tv_phone_number);
            imageView = view.findViewById(R.id.img_photo);
            layout = view.findViewById(R.id.box);
            tvHidden = view.findViewById(R.id.tv_hidden);
        }
    }





}
package com.example.birtdayapp2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.birtdayapp2.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import helper.dbHelper;
import helper.modelHelper;

public class EditActivity extends AppCompatActivity
{
    private dbHelper dbHelper;
    private EditText etName, etPhone, etEmail, etStatus;
    private Button btnUpdate, btnDelete, etBirthDate, etTimer;
    private ImageView imageView;
    private modelHelper contact;
    Uri imageUri = null;
    byte[] imageBytes = null;
    byte[] bytes = null;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new dbHelper(this);

        etName = findViewById(R.id.edt_name);
        etPhone = findViewById(R.id.edt_phone);
        etEmail = findViewById(R.id.edt_email);
        etStatus = findViewById(R.id.edt_status);
        etBirthDate = findViewById(R.id.edt_birth_date);
        etTimer = findViewById(R.id.edt_time);

        btnUpdate = findViewById(R.id.btn_submit);
        btnDelete = findViewById(R.id.btn_delete);

        imageView = findViewById(R.id.edt_image);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        Intent intent = getIntent();
        contact = (modelHelper) intent.getSerializableExtra("contact");

        bytes = contact.getImage();

        etName.setText(contact.getName());
        etPhone.setText(contact.getPhone_number());
        etEmail.setText(contact.getEmail());
        etStatus.setText(contact.getStatus());
        etBirthDate.setText(contact.getBirth_date());
        etTimer.setText(contact.getTime());

        id = contact.getId();

        if(bytes != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phone_number = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                String status = etStatus.getText().toString();
                String birth_date = etBirthDate.getText().toString();
                String time = etTimer.getText().toString();

                if(!validateName(name) || !validatePhoneNumber(phone_number)){
                    return;
                }

                try{

                    if(imageUri != null){
                        imageBytes = getBytes(EditActivity.this, imageUri);
                    }else{
                        imageBytes = bytes;
                    }

                    dbHelper.update(id, name, phone_number, email, imageBytes, status, birth_date, time);
                    Toast.makeText(getApplicationContext(), "Contact updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "error: " + e, Toast.LENGTH_LONG).show();
                }

            }
        });



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext()

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        etBirthDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        etTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,hour,minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        etTimer.setText(sdf.format(calendar.getTime()));

                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });
    }

    public void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(intent);
    }

    public boolean validateName(String name)
    {
        if(name.isEmpty()){
            etName.setError("Name must be filled.");
            return false;
        }else{
            return true;
        }
    }

    public boolean validatePhoneNumber(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");

        if(phoneNumber.isEmpty()){
            etPhone.setError("Phone Number must be filled.");
            return false;
//        }else if(!pattern.matcher(phoneNumber).matches()){
//            etPhone.setError("Phone Number must be number.");
//            return false;
        }else{
            return true;
        }
    }

//    public boolean validateEmail(String email)
//    {
//        Pattern pattern = Pattern.compile(" ^[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+$ ");
//
//        if(!email.isEmpty()){
//            if(!pattern.matcher(email).matches()){
//                etEmail.setError("Email invalid.");
//                return false;
//            }else{
//                return true;
//            }
//        }else{
//            return true;
//        }
//    }

    public void showConfirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("Delete confirmation");
        builder.setMessage("This contact will be deleted form your device");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.delete(id, imageUri);
                Toast.makeText(EditActivity.this, "Contact successfully deleted.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }


    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent  = result.getData();
                            if(intent != null && intent.getData() != null){
                                imageUri = intent.getData();
                                imageView.setImageURI(imageUri);
                            }
                        }
                    }
            );
}
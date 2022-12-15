package com.example.birtdayapp2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import adapter.TimePickerFragment;
import helper.dbHelper;

public class CreateActivity extends AppCompatActivity {
    dbHelper dbHelper;
    private EditText edtName, edtPhoneNumber, edtEmail, edtStatus, edtAddress, edtSocialMedia;
    private ImageView imageView;
    private Button btnSave, edtBirthDate, edtTimer;
    byte[] bytes = null;
    Uri imageUri = null;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dbHelper = new dbHelper(this);

        edtName = findViewById(R.id.edt_name);
        edtPhoneNumber = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtStatus = findViewById(R.id.edt_status);
        edtBirthDate = findViewById(R.id.edt_birth_date);
        edtTimer = findViewById(R.id.edt_time);

        btnSave = findViewById(R.id.btn_submit);

        imageView = findViewById(R.id.imageView);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int maxYear = year +1;
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String phone_number = edtPhoneNumber.getText().toString();
                String email = edtEmail.getText().toString();
                String status = edtStatus.getText().toString();
                String birth_date = edtBirthDate.getText().toString();
                String time = edtTimer.getText().toString();

                if(!validateName(name) || !validatePhoneNumber(phone_number)){
                    return;
                }

                try{
                    if(imageUri != null){
                        bytes = getBytes(CreateActivity.this, imageUri);
                    }

                    dbHelper.store(name, phone_number, email, bytes, status, birth_date, time);

                    Toast.makeText(getApplicationContext(), "Contact created successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                    startActivity(intent);

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext()

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                         CreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day+"/"+month+"/"+year;
                        edtBirthDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edtTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,hour,minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        edtTimer.setText(sdf.format(calendar.getTime()));

                    }
                }, 12, 0, false);
                timePickerDialog.show();
            }
        });
    }



    public boolean validateName(String name)
    {
        if(name.isEmpty()){
            edtName.setError("Name must be filled.");
            return false;
        } else{
            return true;
        }
    }

    public boolean validatePhoneNumber(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");

        if(phoneNumber.isEmpty()){
            edtPhoneNumber.setError("Phone Number must be filled.");
            return false;
//        }else if(!pattern.matcher(phoneNumber).matches()){
//            edtPhoneNumber.setError("Phone Number must be number.");
//            return false;
        }else{
            return true;
        }
    }

//    public boolean validateEmail(String email)
//    {
//        Pattern pattern = Pattern.compile("^[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+$");
//        if(!email.isEmpty()){
//            if(!pattern.matcher(email).matches()){
//                edtEmail.setError("Email invalid.");
//                return false;
//            }else{
//                return true;
//            }
//        }else{
//            return true;
//        }
//
//    }

    public void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(intent);
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
        int bufferSize = 2048;
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
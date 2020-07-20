package advance.bike.security.system;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private BroadcastReceiver sendBroadCastReceiver,deliveredBroadCastReceiver;
    private PendingIntent sentPI,deliveredPI;
    private GifStoppingThread gifStoppingThread;
    private GifImageView gifImageView;
    private Button inputDeviceNumberButton
            , changePasswordButton
            ,saveDeviceNumberDialogButton
            ,changePasswordDialogButton
            ,inputDeviceNumberAlertDialogSaveButton
            ,changePasswordAlertDialogChangePasswordButton;
    private EditText inputDeviceNumberAlertDialogDeviceNumberEditText
            ,changePasswordAlertDialogCurrentPasswordEditText
            ,changePasswordAlertDialogNewPasswordEditText;
    private String dialogType;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        initAll();

        initAndRegisterBroadCastReceiver();


    }

    private void initAll() {
        inputDeviceNumberButton=findViewById(R.id.settingActivityInputDeviceNumberButtonId);
        changePasswordButton=findViewById(R.id.settingActivityChangePasswordButtonId);
        gifImageView=findViewById(R.id.settingActivityGifImageViewId);
        gifImageView.setVisibility(View.GONE);

        inputDeviceNumberButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
    }

    private void initAndRegisterBroadCastReceiver() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        sendBroadCastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                if (gifStoppingThread!=null){
                    gifStoppingThread.interrupt();
                    gifStoppingThread=null;
                }
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        showGifImage("success.gif");
                        gifStoppingThread=new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        showGifImage("failed.gif");
                        gifStoppingThread=new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        showGifImage("failed.gif");
                        gifStoppingThread=new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        showGifImage("failed.gif");
                        gifStoppingThread=new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        showGifImage("failed.gif");
                        gifStoppingThread=new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                }
            }
        };
        registerReceiver(sendBroadCastReceiver,new IntentFilter(SENT));

        //---when the SMS has been delivered---

        deliveredBroadCastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(arg0, "Delivered successfully.", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(arg0, "Delivered failed.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        registerReceiver(deliveredBroadCastReceiver, new IntentFilter(DELIVERED));
    }

    private void showGifImage(String fileName) {
        try {
            GifDrawable gifFromAssets = new GifDrawable( getAssets(), fileName );
            gifImageView.setImageDrawable(gifFromAssets);
            gifImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            gifImageView.setVisibility(View.GONE);
            Toast.makeText(this, "Animation starting failed for "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog() {
        View view=null;
        switch (dialogType){
            case Constants.inputDeviceNumberDialogType:
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_input_device_number,null,false);
                inputDeviceNumberAlertDialogSaveButton=view.findViewById(R.id.inputDeviceNumberAlertDialogSaveButtonId);
                inputDeviceNumberAlertDialogDeviceNumberEditText=view.findViewById(R.id.inputDeviceNumberAlertDialogDeviceNumberEditTextId);

                inputDeviceNumberAlertDialogSaveButton.setOnClickListener(this);
                break;

            case Constants.changePasswordDialogType:
                view=getLayoutInflater().inflate(R.layout.custom_layout_for_change_password,null,false);
                changePasswordAlertDialogCurrentPasswordEditText=view.findViewById(R.id.changePasswordAlertDialogCurrentPasswordEditTextId);
                changePasswordAlertDialogNewPasswordEditText=view.findViewById(R.id.changePasswordAlertDialogNewPasswordEditTextId);
                changePasswordAlertDialogChangePasswordButton=view.findViewById(R.id.changePasswordAlertDialogChangePasswordButtonId);

                changePasswordAlertDialogChangePasswordButton.setOnClickListener(this);
                break;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
        builder.setCancelable(true);
        builder.setView(view);
        alertDialog=builder.create();
        if (alertDialog.getWindow()!=null){
            alertDialog.getWindow().getAttributes().windowAnimations=R.style.DialogTheme;
        }
        if (!isFinishing()){
            alertDialog.show();
        }
    }

    public void sendSmsToDeviceNumber(String devicePhoneNumber,String smsCommand) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(devicePhoneNumber, null, smsCommand, sentPI, deliveredPI);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message for "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startSaveDeviceNumberOperation() {
        String deviceNumber=inputDeviceNumberAlertDialogDeviceNumberEditText.getText().toString();
        if (TextUtils.isEmpty(deviceNumber)){
            inputDeviceNumberAlertDialogDeviceNumberEditText.setError("Please input device number properly.");
            inputDeviceNumberAlertDialogDeviceNumberEditText.setFocusable(true);
        }else {
            Utility.setStringToStorage(SettingActivity.this,Constants.deviceNumberKey,deviceNumber);
            alertDialog.dismiss();
            sendSmsToDeviceNumber(deviceNumber,"Number 1");
            showGifImage("flying_bird_with_latter.gif");
        }
    }

    private void startChangePasswordOperation() {
        String savedPassword=Utility.getStringFromStorage(SettingActivity.this,Constants.userSavedPasswordKey,null);
        String currentPassword=changePasswordAlertDialogCurrentPasswordEditText.getText().toString();
        String newPassword=changePasswordAlertDialogNewPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(currentPassword)){
            changePasswordAlertDialogCurrentPasswordEditText.setError("Please input current password properly.");
            changePasswordAlertDialogCurrentPasswordEditText.setFocusable(true);
        }
        if (TextUtils.isEmpty(newPassword)){
            changePasswordAlertDialogNewPasswordEditText.setError("Please input new password properly.");
            changePasswordAlertDialogNewPasswordEditText.setFocusable(true);
        }

        if (!TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(newPassword)){
            if (currentPassword.equalsIgnoreCase(savedPassword)){
                Utility.setStringToStorage(SettingActivity.this,Constants.userSavedPasswordKey,newPassword);
                Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }else {
                Toast.makeText(this, "Your inputted current password is incorrect, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingActivityInputDeviceNumberButtonId:
                dialogType=Constants.inputDeviceNumberDialogType;
                showAlertDialog();
                break;

            case R.id.inputDeviceNumberAlertDialogSaveButtonId:
                startSaveDeviceNumberOperation();
                break;

            case R.id.settingActivityChangePasswordButtonId:
                dialogType=Constants.changePasswordDialogType;
                showAlertDialog();
                break;

            case R.id.changePasswordAlertDialogChangePasswordButtonId:
                startChangePasswordOperation();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (sendBroadCastReceiver!=null){
            unregisterReceiver(sendBroadCastReceiver);
            sendBroadCastReceiver=null;
        }
        if (deliveredBroadCastReceiver!=null){
            unregisterReceiver(deliveredBroadCastReceiver);
            deliveredBroadCastReceiver=null;
        }
        if (gifStoppingThread!=null){
            gifStoppingThread.interrupt();
            gifStoppingThread=null;
        }
        super.onDestroy();
    }


    class GifStoppingThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2500);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gifImageView.setVisibility(View.GONE);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
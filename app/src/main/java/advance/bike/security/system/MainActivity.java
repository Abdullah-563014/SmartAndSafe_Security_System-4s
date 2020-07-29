package advance.bike.security.system;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import advance.bike.security.system.model.AdminNoticeModel;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private DatabaseReference databaseReference;
    private RadioGroup radioGroup;
    private String currentActivatedOption, devicePhoneNumber;
    private ConstraintLayout smsOptionRootLayout, callOptionRootLayout;
    private ImageView adminNoticeImageView;
    private GifImageView gifImageView;
    private Button lockSmsButton, unLockSmsButton, alarmOffSmsButton, alarmOnSmsButton, statusSmsButton, locationSmsButton, remoteOnSmsButton, remoteOffSmsButton, whiteListOnSmsButton, whiteListOffSmsButton, sensorHighSmsButton, sensorLowSmsButton, autoLockSmsButton, manualLockSmsButton, lockCallButton, unLockCallButton, alarmOffCallButton, alarmOnCallButton, statusCallButton, locationCallButton, settingButton, voiceCommandButton;
    private AdminNoticeModel adminNoticeModel;
    private PendingIntent sentPI, deliveredPI;
    private TextToSpeech textToSpeech;
    private BroadcastReceiver sendBroadCastReceiver, deliveredBroadCastReceiver;
    private GifStoppingThread gifStoppingThread;
    private int speechToTextRequestCode = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initAll();

        getAdminMessageFromDatabase();

        initAllPermission();

        initTextToSpeech();

        initAndRegisterBroadCastReceiver();


    }

    private void initAll() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        radioGroup = findViewById(R.id.mainActivityRadioGroupId);
        smsOptionRootLayout = findViewById(R.id.smsOptionRootLayoutId);
        callOptionRootLayout = findViewById(R.id.callOptionRootLayoutId);
        adminNoticeImageView = findViewById(R.id.mainActivityAdminNoticeImageViewId);
        lockSmsButton = findViewById(R.id.mainActivityLockSmsButtonId);
        unLockSmsButton = findViewById(R.id.mainActivityUnLockSmsButtonId);
        alarmOffSmsButton = findViewById(R.id.mainActivityAlarmOffSmsButtonId);
        alarmOnSmsButton = findViewById(R.id.mainActivityAlarmOnSmsButtonId);
        statusSmsButton = findViewById(R.id.mainActivityStatusSmsButtonId);
        locationSmsButton = findViewById(R.id.mainActivityLocationSmsButtonId);
        remoteOnSmsButton = findViewById(R.id.mainActivityRemoteOnSmsButtonId);
        remoteOffSmsButton = findViewById(R.id.mainActivityRemoteOffSmsButtonId);
        whiteListOnSmsButton = findViewById(R.id.mainActivityWhiteListOnSmsButtonId);
        whiteListOffSmsButton = findViewById(R.id.mainActivityWhiteListOffSmsButtonId);
        sensorHighSmsButton = findViewById(R.id.mainActivitySensorHighSmsButtonId);
        sensorLowSmsButton = findViewById(R.id.mainActivitySensorLowSmsButtonId);
        manualLockSmsButton = findViewById(R.id.mainActivityManualLockSmsButtonId);
        autoLockSmsButton = findViewById(R.id.mainActivityAutoLockSmsButtonId);
        lockCallButton = findViewById(R.id.mainActivityLockCallButtonId);
        unLockCallButton = findViewById(R.id.mainActivityUnLockCallButtonId);
        alarmOffCallButton = findViewById(R.id.mainActivityAlarmOffCallButtonId);
        alarmOnCallButton = findViewById(R.id.mainActivityAlarmOnCallButtonId);
        statusCallButton = findViewById(R.id.mainActivityStatusCallButtonId);
        locationCallButton = findViewById(R.id.mainActivityLocationCallButtonId);
        gifImageView = findViewById(R.id.mainActivityGifImageViewId);
        settingButton = findViewById(R.id.mainActivitySettingButtonId);
        voiceCommandButton = findViewById(R.id.mainActivityVoiceCommandButtonId);


        gifImageView.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(this);
        adminNoticeImageView.setOnClickListener(this);
        lockSmsButton.setOnClickListener(this);
        unLockSmsButton.setOnClickListener(this);
        alarmOffSmsButton.setOnClickListener(this);
        alarmOnSmsButton.setOnClickListener(this);
        statusSmsButton.setOnClickListener(this);
        locationSmsButton.setOnClickListener(this);
        remoteOnSmsButton.setOnClickListener(this);
        remoteOffSmsButton.setOnClickListener(this);
        whiteListOnSmsButton.setOnClickListener(this);
        whiteListOffSmsButton.setOnClickListener(this);
        sensorHighSmsButton.setOnClickListener(this);
        sensorLowSmsButton.setOnClickListener(this);
        manualLockSmsButton.setOnClickListener(this);
        autoLockSmsButton.setOnClickListener(this);
        lockCallButton.setOnClickListener(this);
        unLockCallButton.setOnClickListener(this);
        alarmOffCallButton.setOnClickListener(this);
        alarmOnCallButton.setOnClickListener(this);
        statusCallButton.setOnClickListener(this);
        locationCallButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        voiceCommandButton.setOnClickListener(this);
    }

    private void initAllPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()) {
                    Toast.makeText(MainActivity.this, "Sorry, You need to grant all permission to use this app.", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void showGifImage(String fileName) {
        try {
            GifDrawable gifFromAssets = new GifDrawable(getAssets(), fileName);
            gifImageView.setImageDrawable(gifFromAssets);
            gifImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            gifImageView.setVisibility(View.GONE);
            Toast.makeText(this, "Animation starting failed for " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.setSpeechRate(0.3f);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Initialization Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeASpeech(String command) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(command, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(command, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void startSmsOperation(String smsCommand, String speechCommand) {
        vibrateCreation();
        if (isDeviceNumberValid()) {
            makeASpeech(speechCommand);
            sendSmsToDeviceNumber(smsCommand);
            showGifImage("flying_bird_with_latter.gif");
        } else {
            makeASpeech("device number is not valid. please input valid device number.");
        }
    }

    private void startCallingOperation(String dtmfCommand, String speechCommand) {
        vibrateCreation();
        if (isDeviceNumberValid()) {
            makeASpeech(speechCommand);
            makeCallToDeviceNumber(dtmfCommand);
        } else {
            makeASpeech("device number is not valid. please input valid device number.");
        }
    }

    private void makeCallToDeviceNumber(String dtmfCommand) {
        String callNumber = devicePhoneNumber + "," + dtmfCommand;
        Uri uri = Uri.parse("tel:" + callNumber);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);
    }

    private void vibrateCreation() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public void sendSmsToDeviceNumber(String smsCommand) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(devicePhoneNumber, null, smsCommand, sentPI, deliveredPI);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message for " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isDeviceNumberValid() {
        if (devicePhoneNumber == null || TextUtils.isEmpty(devicePhoneNumber)) {
            return false;
        }
        try {
            double num = Double.parseDouble(devicePhoneNumber);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void initAndRegisterBroadCastReceiver() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        sendBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                if (gifStoppingThread != null) {
                    gifStoppingThread.interrupt();
                    gifStoppingThread = null;
                }
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        showGifImage("success.gif");
                        gifStoppingThread = new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        showGifImage("failed.gif");
                        gifStoppingThread = new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        showGifImage("failed.gif");
                        gifStoppingThread = new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        showGifImage("failed.gif");
                        gifStoppingThread = new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        showGifImage("failed.gif");
                        gifStoppingThread = new GifStoppingThread();
                        gifStoppingThread.start();
                        break;
                }
            }
        };
        registerReceiver(sendBroadCastReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---

        deliveredBroadCastReceiver = new BroadcastReceiver() {
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

    private void updateOptionLayout() {
        if (currentActivatedOption.equalsIgnoreCase("CallOption")) {
            smsOptionRootLayout.setVisibility(View.GONE);
            callOptionRootLayout.setVisibility(View.VISIBLE);
        } else {
            callOptionRootLayout.setVisibility(View.GONE);
            smsOptionRootLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getAdminMessageFromDatabase() {
        databaseReference.child("notice").child("admin_notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    adminNoticeModel = dataSnapshot.getValue(AdminNoticeModel.class);
                    if (adminNoticeModel != null) {
                        Picasso.get().load(adminNoticeModel.getImageUrl()).fit().placeholder(R.drawable.email).error(R.drawable.email).into(adminNoticeImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Admin notice loading failed for " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkMandatoryUpdate() {
        databaseReference.child("version_code").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int version = 1;
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    version = Integer.parseInt(dataSnapshot.getValue(String.class));
                }

                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                int appVersion = 1;
                if (info != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        appVersion = (int) info.getLongVersionCode();
                    } else {
                        appVersion = info.versionCode;
                    }
                }

                if (appVersion < version) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Found New Update");
                    builder.setMessage("Your app is not updated. Please update now.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                            startActivity(browserIntent);
                        }
                    });
                    builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finishAffinity();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    if (!isFinishing()) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openUrl(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(Intent.createChooser(i, "Please select a browser"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open url for " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.mainActivityCallOptionRadioButtonId:
                currentActivatedOption = "CallOption";
                updateOptionLayout();
                break;

            case R.id.mainActivitySmsOptionRadioButtonId:
                currentActivatedOption = "SmsOption";
                updateOptionLayout();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkMandatoryUpdate();
        devicePhoneNumber = Utility.getStringFromStorage(MainActivity.this, Constants.deviceNumberKey, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainActivityAdminNoticeImageViewId:
                if (adminNoticeModel != null && adminNoticeModel.getTargetUrl() != null) {
                    openUrl(adminNoticeModel.getTargetUrl());
                } else {
                    Toast.makeText(this, "Sorry, unfortunately target url is not detecting.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.mainActivityLockSmsButtonId:
                startSmsOperation(Constants.lockSmsCommand, "lock");
                break;

            case R.id.mainActivityUnLockSmsButtonId:
                startSmsOperation(Constants.unLockSmsCommand, "unlock");
                break;

            case R.id.mainActivityAlarmOffSmsButtonId:
                startSmsOperation(Constants.alarmOffSmsCommand, "alarm off");
                break;

            case R.id.mainActivityAlarmOnSmsButtonId:
                startSmsOperation(Constants.alarmOnSmsCommand, "alarm on");
                break;

            case R.id.mainActivityStatusSmsButtonId:
                startSmsOperation(Constants.statusSmsCommand, "status");
                break;

            case R.id.mainActivityLocationSmsButtonId:
                startSmsOperation(Constants.locationSmsCommand, "location");
                break;

            case R.id.mainActivityRemoteOnSmsButtonId:
                startSmsOperation(Constants.remoteOnSmsCommand, "remote on");
                break;

            case R.id.mainActivityRemoteOffSmsButtonId:
                startSmsOperation(Constants.remoteOffSmsCommand, "remote off");
                break;

            case R.id.mainActivityWhiteListOffSmsButtonId:
                startSmsOperation(Constants.whiteListOffSmsCommand, "white list off");
                break;

            case R.id.mainActivityWhiteListOnSmsButtonId:
                startSmsOperation(Constants.whiteListOnSmsCommand, "white list on");
                break;

            case R.id.mainActivitySensorHighSmsButtonId:
                startSmsOperation(Constants.sensorHighSmsCommand, "sensor high");
                break;

            case R.id.mainActivitySensorLowSmsButtonId:
                startSmsOperation(Constants.sensorLowSmsCommand, "sensor low");
                break;

            case R.id.mainActivityManualLockSmsButtonId:
                startSmsOperation(Constants.manualLockSmsCommand, "manual lock");
                break;

            case R.id.mainActivityAutoLockSmsButtonId:
                startSmsOperation(Constants.autoLockSmsCommand, "auto lock");
                break;

            case R.id.mainActivityLockCallButtonId:
                startCallingOperation(Constants.lockCallingCommand + "," + Constants.lockCallingCommand + "," + Constants.lockCallingCommand, "lock");
                break;

            case R.id.mainActivityUnLockCallButtonId:
                startCallingOperation(Constants.unlockCallingCommand + "," + Constants.unlockCallingCommand + "," + Constants.unlockCallingCommand, "unlock");
                break;

            case R.id.mainActivityAlarmOffCallButtonId:
                startCallingOperation(Constants.alarmOffCallingCommand + "," + Constants.alarmOffCallingCommand + "," + Constants.alarmOffCallingCommand, "alarm off");
                break;

            case R.id.mainActivityAlarmOnCallButtonId:
                startCallingOperation(Constants.alarmOnCallingCommand + "," + Constants.alarmOnCallingCommand + "," + Constants.alarmOnCallingCommand, "alarm on");
                break;

            case R.id.mainActivityStatusCallButtonId:
                startCallingOperation(Constants.statusCallingCommand + "," + Constants.statusCallingCommand + "," + Constants.statusCallingCommand, "status");
                break;

            case R.id.mainActivityLocationCallButtonId:
                startCallingOperation(Constants.locationCallingCommand + "," + Constants.locationCallingCommand + "," + Constants.locationCallingCommand, "location");
                break;

            case R.id.mainActivitySettingButtonId:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;

            case R.id.mainActivityVoiceCommandButtonId:
                vibrateCreation();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, speechToTextRequestCode);
                } else {
                    Toast.makeText(MainActivity.this, "Your device don't support voice command", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == speechToTextRequestCode) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = null;
                if (data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                } else {
                    return;
                }
                for (int i = 0; i < result.size(); i++) {
                    String value = result.get(i).toLowerCase();
                    if (value.contains("unlock")) {
                        startSmsOperation(Constants.unLockSmsCommand,"ok, trying to unlock your bike");
                    }else if (value.contains("lock")) {
                        startSmsOperation(Constants.lockSmsCommand,"ok, trying to lock your bike");
                    }else if (value.contains("status")) {
                        startSmsOperation(Constants.statusSmsCommand,"ok, trying to retrieve your bike status");
                    }else if (value.contains("location")) {
                        startSmsOperation(Constants.locationSmsCommand,"ok, trying to retrieve your bike location");
                    }else if (value.contains("alarm off") || value.contains("alarm of")) {
                        startSmsOperation(Constants.alarmOffSmsCommand,"ok, trying to turn off your bike alarm");
                    }else if (value.contains("alarm one") || value.contains("alarm on")) {
                        startSmsOperation(Constants.alarmOnSmsCommand,"ok, trying to turn on your bike alarm");
                    }else if (value.contains("remove off") || value.contains("remote of")) {
                        startSmsOperation(Constants.remoteOffSmsCommand,"ok, trying to turn off your bike remote");
                    }else if (value.contains("remove on") || value.contains("remote one")) {
                        startSmsOperation(Constants.remoteOnSmsCommand,"ok, trying to turn on your bike remote");
                    }else if (value.contains("white list off") || value.contains("white list of") || value.contains("wait list off") || value.contains("wait list of")) {
                        startSmsOperation(Constants.whiteListOffSmsCommand,"ok, trying to turn off white list");
                    }else if (value.contains("white list on") || value.contains("white list one") || value.contains("wait list on") || value.contains("wait list one")) {
                        startSmsOperation(Constants.whiteListOnSmsCommand,"ok, trying to turn on white list");
                    }else if (value.contains("sensor low")) {
                        startSmsOperation(Constants.sensorLowSmsCommand,"ok, trying to low your bike sensor");
                    }else if (value.contains("sensor high") || value.contains("sensor hi")) {
                        startSmsOperation(Constants.sensorHighSmsCommand,"ok, trying to high your bike sensor");
                    }else if (value.contains("manual lock")) {
                        startSmsOperation(Constants.manualLockSmsCommand,"ok, trying to turn on manual lock");
                    }else if (value.contains("auto lock")) {
                        startSmsOperation(Constants.autoLockSmsCommand,"ok, trying to turn on auto lock");
                    }
                }
            } else {
                Toast.makeText(this, "Sorry, speech recognition failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (sendBroadCastReceiver != null) {
            unregisterReceiver(sendBroadCastReceiver);
            sendBroadCastReceiver = null;
        }
        if (deliveredBroadCastReceiver != null) {
            unregisterReceiver(deliveredBroadCastReceiver);
            deliveredBroadCastReceiver = null;
        }
        if (gifStoppingThread != null) {
            gifStoppingThread.interrupt();
            gifStoppingThread = null;
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
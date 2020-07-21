package advance.bike.security.system.myreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import advance.bike.security.system.Constants;
import advance.bike.security.system.Utility;

public class SmsReceiver extends BroadcastReceiver {

    String smsServerAddress,smsBodyFromServer;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction()!=null && intent.getAction().equals(SMS_RECEIVED)){
            Bundle pudsBundle=intent.getExtras();
            Object[] pdus= (Object[]) pudsBundle.get("pdus");
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) pdus[0]);
            smsServerAddress=smsMessage.getOriginatingAddress();
            smsBodyFromServer=smsMessage.getMessageBody();



            String phoneNumber= Utility.getStringFromStorage(context, Constants.deviceNumberKey,null);
            if (phoneNumber.contains(smsServerAddress) || smsServerAddress.contains(phoneNumber)){
                if (smsBodyFromServer.contains("https://www.google.com")){
                    showGoogleMap(context,smsBodyFromServer);
                }
                else {
                    Toast.makeText(context, "Url Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void showGoogleMap(Context context, String url) {
        int lastIndexOfForwardSplash=url.lastIndexOf("/");
        String latAndLon=url.substring(lastIndexOfForwardSplash+1);
        try {
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW);
            mapIntent.setData(Uri.parse("geo:0,0?q=" + latAndLon));
            context.startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Url not valid", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.inspiron3847desktop.voipexample;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("libsinch-android-rtc.so");
    }

    Button callButton;
    TextView callState;

    Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final SinchClient sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId("current-user-id")
                .applicationKey("a8461e81-14cd-4efd-83a9-b7c2e3377fb7")
                .applicationSecret("eAexTpFzzkSbNUcY1edB8w==")
                .environmentHost("sandbox.sinch.com")
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.start();

        callState = (TextView) findViewById(R.id.callState);
        callButton = (Button) findViewById(R.id.callButton);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(call==null){
                    call = sinchClient.getCallClient().callPhoneNumber("+12532177274");
                    callButton.setText("Hang Up");
                    call.addCallListener(new SinchCallListener());
                } else {
                    call.hangup();
                }

            }
        });


    }




    private class SinchCallListener implements CallListener {
        //the call is ended for any reason
        @Override
        public void onCallEnded(Call endedCall) {
            call = null; //no longer a current call
            callButton.setText("Call"); //change text on button
            callState.setText(""); //empty call state
            //hardware volume buttons should revert to their normal function
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        //call is connected
        @Override
        public void onCallEstablished(Call establishedCall) {
            //change the call state in the view
            callState.setText("connected");
            //the hardware volume buttons should control the voice stream volume
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }
        //call is trying to connect
        @Override
        public void onCallProgressing(Call progressingCall) {
            //set call state to "ringing" in the view
            callState.setText("ringing");
        }
        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //intentionally left empty
        }
    }

}

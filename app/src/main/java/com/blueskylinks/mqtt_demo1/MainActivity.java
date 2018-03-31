package com.blueskylinks.mqtt_demo1;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    TextView t1;
    MqttClient sampleClient;
    ToneGenerator toneGenerator1;
    int lr[]={1,2,3};
    MqttMessage Mmessage1;
    MqttMessage Mmessage2;
    MqttMessage Mmessage3;
    int volume=70;
    int streamType = AudioManager.STREAM_MUSIC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toneGenerator1=new ToneGenerator(streamType,volume);
    }

    @Override
    public void onResume(){
        super.onResume();
        MyReceiver1 myReceiver;
        myReceiver = new MyReceiver1();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CUSTOM_INTENT");
        registerReceiver(myReceiver, intentFilter);
    }
    public void msg_pub(View view)throws MqttException {
        String topic = "home";

        Mmessage1 = new MqttMessage();
        Mmessage2 = new MqttMessage();
        Mmessage3 = new MqttMessage();
        JSONObject jsonObject=new JSONObject();
        try {

        jsonObject.put("lr[0]",lr[0]);
        jsonObject.put("lr[1]",lr[1]);
        String json=String.valueOf(jsonObject);
        Mmessage1.setPayload(json.getBytes());
            sampleClient.publish(topic, Mmessage1);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();}
        catch (MqttException e) {
            e.printStackTrace();}

        Log.i("message sending ", String.valueOf(Mmessage1));
        try {
            int j=jsonObject.getInt("lr[1]");
            Log.i("....:",String.valueOf(j));
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* t1=findViewById(R.id.tv);
        String s= (String) t1.getText();
        Log.i("data",s);
        Mmessage2.setPayload(s.getBytes());
        if(s=="{"1":2}"){ sampleClient.publish("test",Mmessage2);
            Log.i("..","msg sent to gateway");}*/

    }


    public void mqtt_sub(View v1)  {

        MqttCallback mqtt_callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //t1.setText(mqttMessage.toString());
                Intent intent = new Intent();
                intent.setAction("CUSTOM_INTENT");
                intent.putExtra("D1", mqttMessage.toString());

                sendBroadcast(intent);

                String s1= mqttMessage.toString();

               Log.i(s,mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

        String broker       = "tcp://13.126.9.228:1883";
        String clientId     = "4";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Log.i("Connecting to broker: ", broker);
            sampleClient.connect(connOpts);
            Log.i("Connected", "C");
            sampleClient.setCallback(mqtt_callback);
            sampleClient.subscribe("home");


        } catch(MqttException me) {
            Log.i("reason ",String.valueOf(me.getReasonCode()));
            Log.i("msg ",String.valueOf(me.getMessage()));
            Log.i("loc ",String.valueOf(me.getLocalizedMessage()));
            Log.i("cause ",String.valueOf(me.getCause()));
            Log.i("excep ",String.valueOf(me));
            me.printStackTrace();
        }

    }

    public class MyReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        //    b1=findViewById(R.id.button2);
            //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
            String s1=arg1.getStringExtra("D1");

           t1 = (TextView) findViewById(R.id.tv);

            t1.setText(s1);
               Log.i("BLE,,,,,,,",s1);
            /*switch(s1){
                case "1":
                    warning();
                    b1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    break;
                case "0":
                    b1.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    break;
            }*/
       }

    }

    public void warning(){
        Log.i("..","Alarm is triggered");
       // toneGenerator1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,5000);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notifications Example")
                        .setContentText("Alarm Triggered");
        Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
       // toneGenerator1.stopTone();
    }

}





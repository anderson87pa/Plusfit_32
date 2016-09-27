package com.pucgo.plusfit.communication.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by rafaela on 26/09/2016.
 */
public class MqttService {

    private Context context;
    private MqttConnectOptions options;
    private MqttAndroidClient client;
    private String clientId;
    private IMqttToken token;

    public MqttService(Context context) {
        this.context = context;
    }

    public void connect() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://broker.hivemq.com:1883", clientId);
        options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        try {
            token = client.connect(options);
//            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("tag", "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("tag", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectToLWT(String topic, byte[] payload) throws MqttException {
//        String topic = "users/last/will";
//        byte[] payload = "some payload".getBytes();
        options.setWill(topic, payload, 1, false);
        token = client.connect(options);
    }


    public void connectUserPassword(String user, String password) throws MqttException {
        options.setUserName("USERNAME");
        options.setPassword("PASSWORD".toCharArray());
        token = client.connect(options);
    }

    public void publish(String txt){
        String topic = "foo/bar";
        //String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = txt.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishRetainMessage(String txt){
        String topic = "foo/bar";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = txt.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(){
        String topic = "foo/bar";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(){
        final String topic = "foo/bar";
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void disconnect(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

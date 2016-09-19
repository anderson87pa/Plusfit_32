package com.pucgo.plusfit.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pucgo.plusfit.R;
import com.pucgo.plusfit.controller.Bluetooth;
import com.pucgo.plusfit.controller.ConnectionBluetoothThread;

public class IniciaAtividadeActivity extends AppCompatActivity {

    private Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_atividade);
        bluetooth = new Bluetooth(this);
        bluetooth.conectToDevice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Bluetooth.ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth ativado :D", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Bluetooth não ativado :(", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

//            if(dataString.equals("---N"))
//                statusMessage.setText("Ocorreu um erro durante a conexão D:");
//            else if(dataString.equals("---S"))
//                statusMessage.setText("Conectado :D");

        }
    };
}

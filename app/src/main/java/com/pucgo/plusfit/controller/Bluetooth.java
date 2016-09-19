package com.pucgo.plusfit.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by rafaela.araujo on 19/09/2016.
 */
public class Bluetooth {

    public static int ENABLE_BLUETOOTH = 1;
    public static String DEVICE_NAME = "My_Device_Name";
    private Activity context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private ConnectionBluetoothThread connect;
    private boolean isConnected = false;
    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


    public Bluetooth(Activity context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        solicitaAtivacaoDoBluetooth();
        enableVisibility();
        searchPairedDevices();
    }

    private void solicitaAtivacaoDoBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
//                statusMessage.setText("Solicitando ativação do Bluetooth...");
            }
        } else {
            Toast.makeText(context, "Que pena! Hardware Bluetooth não está funcionando :(", Toast.LENGTH_LONG).show();
        }
    }

    public void searchPairedDevices() {
        bluetoothAdapter.startDiscovery();

        /*  Cria um filtro que captura o momento em que um dispositivo é descoberto.
            Registra o filtro e define um receptor para o evento de descoberta.
         */
        context.registerReceiver(receiver, filter);
        context.registerReceiver(receiver, filter1);
        context.registerReceiver(receiver, filter2);
        context.registerReceiver(receiver, filter3);
    }


    /*  Define um receptor para o evento de descoberta de dispositivo.
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        /*  Este método é executado sempre que um novo dispositivo for descoberto.
         */
        public void onReceive(Context context, Intent intent) {

            /*  Obtem o Intent que gerou a ação.
                Verifica se a ação corresponde à descoberta de um novo dispositivo.
                Obtem um objeto que representa o dispositivo Bluetooth descoberto.
                Exibe seu nome e endereço na lista.
             */
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (!isConnected) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    initConnection(device);
                    Toast.makeText(context, device.getName() + "\n" + device.getAddress(), Toast.LENGTH_LONG).show();
                }

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                isConnected = true;
                Toast.makeText(context, "Dispositivo conectado", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void enableVisibility() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        context.startActivity(discoverableIntent);
    }

    public void conectToDevice() {
        if (!isConnected) {
            BluetoothDevice result = null;

            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    if (DEVICE_NAME.equals(device.getName())) {
                        result = device;
                        break;
                    }
                }
            }
            initConnection(result);
        }
    }

    private void initConnection(BluetoothDevice result) {
        if (result != null) {
            this.device = result;
            connect = new ConnectionBluetoothThread(device.getAddress());
            connect.start();
        }
    }
}

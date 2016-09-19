package com.pucgo.plusfit.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import com.pucgo.plusfit.Activity.IniciaAtividadeActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rafaela.araujo on 19/09/2016.
 */
public class ConnectionBluetoothThread extends Thread {
    private BluetoothSocket btSocket = null;
    private BluetoothServerSocket btServerSocket = null;
    private BluetoothAdapter btAdapter;
    private String btDevAddress = null;
    private String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private boolean server;
    private boolean running = false;


    /*  Este construtor prepara o dispositivo para atuar como servidor.
     */
    public ConnectionBluetoothThread(BluetoothAdapter adapter) {
        this.server = true;
        this.btAdapter = adapter;
    }

    /*  Este construtor prepara o dispositivo para atuar como cliente.
        Tem como argumento uma string contendo o endereço MAC do dispositivo
    Bluetooth para o qual deve ser solicitada uma conexão.
     */
    public ConnectionBluetoothThread(String btDevAddress) {

        this.server = false;
        this.btDevAddress = btDevAddress;
    }

    /*  O método run() contem as instruções que serão efetivamente realizadas
    em uma nova thread.
     */
    public void run() {

        /*  Anuncia que a thread está sendo executada.
            Pega uma referência para o adaptador Bluetooth padrão.
         */
        this.running = true;

        /*  Determina que ações executar dependendo se a thread está configurada
        para atuar como servidor ou cliente.
         */
        if(this.server) {

            /*  Servidor.
             */
            try {

                /*  Cria um socket de servidor Bluetooth.
                    O socket servidor será usado apenas para iniciar a conexão.
                    Permanece em estado de espera até que algum cliente
                estabeleça uma conexão.
                 */
                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("Super Bluetooth", UUID.fromString(myUUID));
                btSocket = btServerSocket.accept();

                /*  Se a conexão foi estabelecida corretamente, o socket
                servidor pode ser liberado.
                 */
                if(btSocket != null) {

                    btServerSocket.close();
                }

            } catch (IOException e) {

                /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                    Envia um código para a Activity principal, informando que
                a conexão falhou.
                 */
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }


        } else {

            /*  Cliente.
             */
            try {

                /*  Obtem uma representação do dispositivo Bluetooth com
                endereço btDevAddress.
                    Cria um socket Bluetooth.
                 */
                BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

                /*  Envia ao sistema um comando para cancelar qualquer processo
                de descoberta em execução.
                 */
                btAdapter.cancelDiscovery();

                /*  Solicita uma conexão ao dispositivo cujo endereço é
                btDevAddress.
                    Permanece em estado de espera até que a conexão seja
                estabelecida.
                 */
                if (btSocket != null)
                    btSocket.connect();

            } catch (IOException e) {

                /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                    Envia um código para a Activity principal, informando que
                a conexão falhou.
                 */
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        }

        /*  Pronto, estamos conectados! Agora, só precisamos gerenciar a conexão.
            ...
        */

    }

    /*  Utiliza um handler para enviar um byte array à Activity principal.
        O byte array é encapsulado em um Bundle e posteriormente em uma Message
    antes de ser enviado.
     */
    private void toMainActivity(byte[] data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        IniciaAtividadeActivity.handler.sendMessage(message);
    }

    /*  Método utilizado pela Activity principal para encerrar a conexão
     */
    public void cancel() {
        try {
            running = false;
            btServerSocket.close();
            btSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }
}

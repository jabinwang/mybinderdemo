package com.jabin.ipcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import com.jabin.ipcdemo.entity.Message;

public class MainActivity extends AppCompatActivity {

    private IConnectService2 connProxy;
    private IServiceManager serviceManager;
    private IMessageService messageServiceProxy;
    private Messenger messengerProxy;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull final android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Message.class.getClassLoader());
            final Message message = bundle.getParcelable("reply");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(MainActivity.this, "client : " + message.getContent(), Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }
    };
    private Messenger clientMessenger = new Messenger(handler);
    private MessageReceiveListener listener = new MessageReceiveListener.Stub() {
        @Override
        public void onReceiveMessage(final Message message) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, message.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {


            try {
                serviceManager = IServiceManager.Stub.asInterface(service);
                connProxy = IConnectService2.Stub.asInterface(serviceManager.getService(IConnectService2.class.getSimpleName()));
                messageServiceProxy = IMessageService.Stub.asInterface(serviceManager.getService(IMessageService.class.getSimpleName()));
                messengerProxy = new Messenger(serviceManager.getService(Messenger.class.getSimpleName()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, RemoteService.class);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    public void connect(View view) {
        try {
            connProxy.connect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(View view) {
        try {
            connProxy.disconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void isconnect(View view) {
        try {
            boolean result = connProxy.isConnect();
            Toast.makeText(MainActivity.this, "connect? " + result, Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(View view) {
        try {
            Message message = new Message();
            message.setContent("hi i am client");
            messageServiceProxy.sendMessage(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void register(View view) {
        try {
            messageServiceProxy.registerMessageReceiveListener(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregister(View view) {
        try {
            messageServiceProxy.unRegisterMessageReceiveListener(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void messenger(View view) {


        try {
            Message message = new Message();
            message.setContent("hi i am from client by messenger");

            android.os.Message msg = new android.os.Message();
            msg.replyTo = clientMessenger;
            Bundle bundle = new Bundle();
            bundle.putParcelable("msg", message);
            msg.setData(bundle);

            messengerProxy.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
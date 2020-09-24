package com.jabin.ipcdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jabin.ipcdemo.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RemoteService extends Service {
    private boolean connected = false;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            //ClassNotFoundException
            bundle.setClassLoader(Message.class.getClassLoader());
            Message message = bundle.getParcelable("msg");
            Toast.makeText(RemoteService.this, "remote : " + message.getContent(), Toast.LENGTH_SHORT).show();

            //reply
            try {
                Messenger clientMessenger = msg.replyTo;
                Message replyMsg = new Message();
                replyMsg.setContent("hi i am remote by messenger");
                android.os.Message clientMsg = new android.os.Message();
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("reply", replyMsg);
                clientMsg.setData(bundle1);
                clientMessenger.send(clientMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private RemoteCallbackList<MessageReceiveListener> remoteCallbackList = new RemoteCallbackList<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture scheduledFuture;
    private Messenger messenger = new Messenger(handler);
    private IConnectService conn1;
    private IConnectService2 conn = new IConnectService2.Stub() {

        @Override
        public void connect() throws RemoteException {
            try {
                Thread.sleep(2000);
                connected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RemoteService.this, "connect", Toast.LENGTH_SHORT).show();
                    }
                });
                scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        int size = remoteCallbackList.beginBroadcast();
                        for (int i = 0 ; i < size; i++) {
                            try {
                                Message message = new Message();
                                message.setContent("hi this msg from server");
                                remoteCallbackList.getBroadcastItem(i).onReceiveMessage(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        remoteCallbackList.finishBroadcast();
                    }
                }, 2000, 5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            connected = false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this, "disconnect", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isConnect() throws RemoteException {
            return connected;
        }
    };


    private IMessageService messageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(final Message message) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this, "remoteService " + message.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
            if (connected) {
                message.setSendSuccess(true);
            }else {
                message.setSendSuccess(false);
            }
        }

        @Override
        public void registerMessageReceiveListener(MessageReceiveListener listener) throws RemoteException {
            if (listener != null) {
                remoteCallbackList.register(listener);
            }
        }

        @Override
        public void unRegisterMessageReceiveListener(MessageReceiveListener listener) throws RemoteException {
            if (listener != null) {
                remoteCallbackList.unregister(listener);
            }
        }
    };

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if (IConnectService.class.getSimpleName().equalsIgnoreCase(serviceName)){
                return conn1.asBinder();
            }else if (IMessageService.class.getSimpleName().equalsIgnoreCase(serviceName)){
                return messageService.asBinder();
            }else if (Messenger.class.getSimpleName().equalsIgnoreCase(serviceName)){
                return messenger.getBinder();
            }else if (IConnectService2.class.getSimpleName().equalsIgnoreCase(serviceName)){
                return conn.asBinder();
            }
            return null;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceManager.asBinder();
    }



    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }
}

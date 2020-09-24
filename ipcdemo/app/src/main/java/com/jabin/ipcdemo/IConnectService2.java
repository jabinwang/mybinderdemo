package com.jabin.ipcdemo;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IConnectService2 extends IInterface {


    public static abstract class Stub extends Binder implements IConnectService2{
        private static final String DESCRIPTOR = "com.jabin.ipcdemo.IConnectService2";
        static final int TRANSACTION_connect = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_disconnect = IBinder.FIRST_CALL_TRANSACTION + 1;
        static final int TRANSACTION_isConnect = IBinder.FIRST_CALL_TRANSACTION + 2;

        public Stub(){
            attachInterface(this, DESCRIPTOR);
        }

        public static IConnectService2 asInterface(IBinder obj){
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && iin instanceof IConnectService2){
                return (IConnectService2) iin;
            }

            return new Proxy(obj);
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
           String descriptor = DESCRIPTOR;
            switch (code){

                case IBinder.INTERFACE_TRANSACTION:
                    data.writeString(descriptor);

                    return  true;
                case TRANSACTION_connect:
                    data.enforceInterface(descriptor);
                    this.connect();
                    return  true;
                case TRANSACTION_disconnect:
                    data.enforceInterface(descriptor);
                    this.disconnect();
                    reply.writeNoException();
                    return  true;
                case TRANSACTION_isConnect:
                    data.enforceInterface(descriptor);
                    boolean result = this.isConnect();
                    reply.writeNoException();
                    reply.writeInt(result? 1 : 0);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        private static class Proxy implements IConnectService2{
            private IBinder remote;
            public Proxy(IBinder obj) {
                this.remote = obj;
            }

            @Override
            public void connect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean status = remote.transact(TRANSACTION_connect, _data, null, IBinder.FLAG_ONEWAY);
                    if (!status){
                        return;
                    }
                }finally {
                    _data.recycle();
                }

            }

            @Override
            public void disconnect() throws RemoteException{
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean status = remote.transact(TRANSACTION_disconnect, _data, _reply, 0);
                    if (!status){
                        return;
                    }
                    _reply.readException();
                }finally {
                    _data.recycle();
                    _reply.recycle();
                }
            }

            @Override
            public boolean isConnect() throws RemoteException{
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                boolean result = false;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean status = remote.transact(TRANSACTION_isConnect, _data, _reply, 0);
                    if (!status){
                        return result;
                    }
                    _reply.readException();
                    result = _reply.readInt() != 0;
                }finally {
                    _data.recycle();
                    _reply.recycle();
                }
                return result;
            }

            @Override
            public IBinder asBinder() {
                return remote;
            }
        }
    }


    void connect() throws RemoteException;
    void disconnect() throws RemoteException;
    boolean isConnect() throws RemoteException;
}

// IConnectService.aidl
package com.jabin.ipcdemo;

// Declare any non-default types here with import statements

interface IConnectService {

    oneway void connect();
    void disconnnect();
    boolean isConnected();
}

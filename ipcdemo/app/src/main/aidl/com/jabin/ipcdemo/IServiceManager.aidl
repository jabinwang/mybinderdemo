// IServiceManager.aidl
package com.jabin.ipcdemo;

// Declare any non-default types here with import statements

interface IServiceManager {
    IBinder getService(String serviceName);
}

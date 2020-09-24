// MessageReceiveListener.aidl
package com.jabin.ipcdemo;
import com.jabin.ipcdemo.entity.Message;
// Declare any non-default types here with import statements

interface MessageReceiveListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void onReceiveMessage(in Message message);
}

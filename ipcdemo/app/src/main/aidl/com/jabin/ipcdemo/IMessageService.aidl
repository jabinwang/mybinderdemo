// IMessageService.aidl
package com.jabin.ipcdemo;
import com.jabin.ipcdemo.entity.Message;
import com.jabin.ipcdemo.MessageReceiveListener;
// Declare any non-default types here with import statements

interface IMessageService {

   void sendMessage(in Message message);
   void registerMessageReceiveListener(MessageReceiveListener listener);
   void unRegisterMessageReceiveListener(MessageReceiveListener listener);
}

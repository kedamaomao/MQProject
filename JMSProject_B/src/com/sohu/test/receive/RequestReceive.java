package com.sohu.test.receive;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class RequestReceive {
    
    public void requestHandle(HashMap<Serializable,Serializable> sendMsg){
        System.out.println("接受到消息了。。。。");
        for (Map.Entry<Serializable,Serializable> entry:sendMsg.entrySet()) {
            System.out.println(entry.getKey()+"............"+entry.getValue());
            
        }
        
    }
    
    public static void main(String[] args) throws JMSException {
        //1.创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616"
                );
        //2.获取连接对象并开启
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //3.获取会话并创建接收者
        Session session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("MyQueue");
        MessageConsumer msgConsumer = session.createConsumer(destination);
        //4.接受消息
        RequestReceive requestReceive = new RequestReceive();
        while (true) {
            ObjectMessage message = (ObjectMessage) msgConsumer.receive(1000);
            if(null != message){
                HashMap<Serializable, Serializable> requestMsg = (HashMap<Serializable, Serializable>) message.getObject();
                requestReceive.requestHandle(requestMsg);
            }else{
                break;
            }
        }
        
        
        
    }
    
    
    
}




package com.sohu.test.send;

import java.io.Serializable;
import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 
 * <p>
 * Description: P2P模式下的发送者
 * 在点对点消息传送模型中，应用程序由消息队列，发送者，接收者组成。每一个消息发送给一个特殊的消息队列，
 * 该队列保存了所有发送给它的消息(除了被接收者消费掉的和过期的消息)。点对点消息模型有一些特性，如下：
            每个消息只有一个接收者；
            消息发送者和接收者并没有时间依赖性；
            当消息发送者发送消息的时候，无论接收者程序在不在运行，都能获取到消息；
            当接收者收到消息的时候，会发送确认收到通知（acknowledgement）。
 * </p>
 * @author zhanghuichao
 * @version 1.0
 * @Date 2017年3月26日
 */
public class SendSubmit {
    //消息发送者
    private MessageProducer send;
    //会话
    private Session session;
    //连接
    private Connection connection;
    //初始化
    public void init() throws Exception{
        //1.创建连接工厂，JMS用它来创建连接
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616");
        //2.从构造工厂中得到连接
        connection = connectionFactory.createConnection();
        //3.开启连接
        connection.start();
        //4.获取session对象
        //创建Session时有两个非常重要的参数，第一个boolean类型的参数用来表示是否采用事务消息。如果是事务消息，对于的参数设置为true，此时消息的提交自动有comit处理，消息的回滚则自动由rollback处理。加入消息不是事务的，则对应的该参数设置为false，此时分为三种情况：
        //Session.AUTO_ACKNOWLEDGE表示Session会自动确认所接收到的消息。
        //Session.CLIENT_ACKNOWLEDGE表示由客户端程序通过调用消息的确认方法来确认所接收到的消息。
        //Session.DUPS_OK_ACKNOWLEDGE使得Session将“懒惰”地确认消息，即不会立即确认消息，这样有可能导致消息重复投递。
        session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
        //5.点对点模式需要创建队列
        Destination destination = session.createQueue("MyQueue");
        //6.得到消息发送者
        send = session.createProducer(destination);
       //设置不持久化
        send.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        
    }
    //发送信息方法
    public void submit(HashMap<Serializable,Serializable> sendMsg) throws JMSException{
        ObjectMessage msg = session.createObjectMessage(sendMsg);
        send.send(msg);
        session.commit();
    }
    
    public static void main(String[] args) {
        SendSubmit sendSubmit = new SendSubmit();
        try {
            sendSubmit.init();
            HashMap<Serializable,Serializable> msgMap = new HashMap<>();
            msgMap.put("testMQ","zhc");
            sendSubmit.submit(msgMap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //关闭资源
            try {
                sendSubmit.connection.close();
                sendSubmit.session.close();
                sendSubmit.send.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
           
        }
        
    }
}





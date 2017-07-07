package MQ.ActiveMQ.helloworld;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * Created by Try on 2017/7/6.
 */
public class Receiver {
    public static void main(String[] args) throws JMSException {
        //建立ConnectionFactory工厂对象，需要填入用户名，密码，连接地址
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://localhost:61616"
        );

        //通过ConnectionFactory工厂对象创建一个Connection连接，并调用Connection的start方法开启连接，Connection默认是关闭
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        //通过Connection对象创建session会话，用于接收消息
        //第一个参数为是否启用事务，使用事务需要session.commit()提交；第二个参数为签收模式
        Session session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);

        //通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息来源的对象，在PTP模式中Destination被称为Queue
        Destination destination = session.createQueue("Queue");
        //Topic pub = session.createTopic("pubs");

        //通过session对象创建消息的发送和接收对象（生产者和消费者）MessageProducer/MessageConsumer
        MessageConsumer messageConsumer = session.createConsumer(destination);

        //消费数据
        while(true){
            TextMessage message = (TextMessage)messageConsumer.receive();
            //手动签收，另起一个线程（tcp）去通知MQ服务确认签收
            message.acknowledge();
            if(message==null){
                break;
            }
            System.out.println(message.getText());
        }


        if (connection != null) {
            connection.close();
        }
    }
}

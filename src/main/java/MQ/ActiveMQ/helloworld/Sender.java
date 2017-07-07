package MQ.ActiveMQ.helloworld;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Try on 2017/7/6.
 */
public class Sender {
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
        //第一个参数为是否启用事务，第二个参数为签收模式,一般设置为自动签收
        Session session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);

        //通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息来源的对象，在PTP模式中Destination被称为Queue
        Destination destination = session.createQueue("Queue");
        //Topic pub = session.createTopic("pubs");

        //通过session对象创建消息的发送和接收对象（生产者和消费者）MessageProducer/MessageConsumer
        MessageProducer messageProducer = session.createProducer(destination);

        //使用MessageProducer的setDeliveryMode方法为其设置特性和持久化特性（DeliveryMode）
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        //生产数据
        TextMessage textMessage = session.createTextMessage();
        for (int i = 1; i <=5 ; i++) {
            textMessage.setText("id="+i+", 消息内容");
            messageProducer.send(textMessage);
            System.out.println("产生一条消息");
        }


        if (connection != null) {
            connection.close();
        }
    }
}

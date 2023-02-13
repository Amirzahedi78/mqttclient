package com.mqttclient.mqttclient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
public class MqttBeans {
    public MqttPahoClientFactory mqttPahoClientFactory(){
        //MQTT connection password, Default password: 12345678
        String pass = "12345678";

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        //Configuring MQTT connection options:
        options.setServerURIs(new String[] {"tcp://localhost:1883"});
        //Default username: admin
        options.setUserName("admin");
        options.setPassword(pass.toCharArray());

        factory.setConnectionOptions(options);
        return factory; //Returns a MQTTClientFactory Object
    }
    @Bean
    public MessageChannel mqttInboundChannel(){
        return new DirectChannel();
    }
    @Bean
    public MessageProducer inbound(){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
                mqttPahoClientFactory(), "#");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        //Default QOS: 0, If handshake is needed: QOS = 1,2
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler handler(){
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
               String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
               //This part depends on the topics which has been determined before(Default: myTopic):
               if(topic.equals("myTopic")) {
                   System.out.println("This is our topic"); //For checking the received topic
               }
               System.out.println(message.getPayload()); //Showing the message's payload
            }
        };
    }
    @Bean
    public MessageChannel mqttOutboundChannel(){
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(){
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut",
                mqttPahoClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("#");
        return messageHandler;
    }
}

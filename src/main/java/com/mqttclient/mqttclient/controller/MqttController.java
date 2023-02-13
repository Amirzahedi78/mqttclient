package com.mqttclient.mqttclient.controller;
import com.google.gson.Gson;
import com.mqttclient.mqttclient.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.jfunc.json.JsonObject;

@RestController
public class MqttController {
    //Injecting MqttGateway interface for using its method:
    @Autowired
    MqttGateway mqttGateway;

    @PostMapping("/sendMessage")
    public ResponseEntity<?> publish(@RequestBody String mqttMessage) {
        try {
            JsonObject convertObject = new Gson().fromJson(mqttMessage, JsonObject.class);
            mqttGateway.sendToMqtt(convertObject.get("message").toString(), convertObject.get("topic").toString());
            return ResponseEntity.ok("Success");
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("Failure");
        }

    }
}

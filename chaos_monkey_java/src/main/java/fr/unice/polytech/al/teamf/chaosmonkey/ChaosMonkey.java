package fr.unice.polytech.al.teamf.chaosmonkey;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.unice.polytech.al.teamf.chaosmonkey.exceptions.ConnectionException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ChaosMonkey {

    private static final ChaosMonkey INSTANCE = new ChaosMonkey();
    private static final Logger log = LoggerFactory.getLogger(ChaosMonkey.class);

    private boolean initialized = false;
    private Map<String, Double> settings;
    private Channel logChannel;

    private ChaosMonkey() {
        settings = new HashMap<>();
    }

    public static ChaosMonkey getInstance() {
        return INSTANCE;
    }

    public void initialize(String chaosMonkeyUrl, String queueUrl) throws ConnectionException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(chaosMonkeyUrl);
        try {
            HttpResponse response = client.execute(request);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(response.getEntity().getContent()));
            jsonObject.forEach((k, v) -> settings.put((String) k, Double.valueOf(((String) v))));
            log.info("Retrieved settings from server: " + settings.toString());
            log.info(settings.size() + " settings retrieved from server");
        } catch (IOException e) {
            throw new ConnectionException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(queueUrl);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("submit_chaos_settings", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "submit_chaos_settings", "");

            logChannel = connection.createChannel();
            channel.exchangeDeclare("chaos_logs_exchange", "fanout");

            log.info("Listening 'submit_chaos_settings' messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
                    jsonObject.forEach((k, v) -> settings.put((String) k, Double.valueOf(((String) v))));
                    log.info("New settings received: " + settings.toString());
                    log.info(settings.size() + " settings received");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            };
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
            initialized = true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            
        }
    }

    public double getSetting(String name) {
        if (!initialized) {
            throw new IllegalStateException("Chaos Monkey framework has not been initialized.");
        }
        return settings.get(name);
    }

    public Draw draw(String setting) {
        return new Draw(getSetting(setting), setting, logChannel);
    }


}
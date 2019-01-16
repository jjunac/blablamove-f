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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ChaosMonkey {

    private static final ChaosMonkey INSTANCE = new ChaosMonkey();

    private boolean initialized = false;
    private Map<String, Double> settings;

    private ChaosMonkey() {
        settings = new HashMap<>();
    }

    public static ChaosMonkey getInstance() {
        return INSTANCE;
    }

    public void intialize(String chaosMonkeyUrl) throws ConnectionException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(chaosMonkeyUrl);
        try {
            HttpResponse response = client.execute(request);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(response.getEntity().getContent()));
            jsonObject.forEach((k, v) -> settings.put((String)k, Double.valueOf(((String)v))));
            System.out.println(settings);
        } catch (IOException e) {
            throw new ConnectionException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("submit_chaos_settings", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "submit_chaos_settings", "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public double getSetting(String name) {
        return settings.get(name);
    }

    public Draw draw(String setting) {
        return new Draw(getSetting(setting));
    }

}
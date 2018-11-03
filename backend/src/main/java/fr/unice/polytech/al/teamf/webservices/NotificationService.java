package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import fr.unice.polytech.al.teamf.entities.Notification;

import java.util.List;

@JsonRpcService("/notification")
public interface NotificationService {
    List<Notification> pullNotificationForUser(@JsonRpcParam(value = "username") String username);
}

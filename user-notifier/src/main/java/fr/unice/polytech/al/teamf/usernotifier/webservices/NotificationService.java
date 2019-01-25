package fr.unice.polytech.al.teamf.usernotifier.webservices;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.exceptions.UnknownUserException;

import java.util.List;

@JsonRpcService("/notification")
public interface NotificationService {
    @JsonRpcErrors({
            @JsonRpcError(exception = UnknownUserException.class,
                    code = 404, message = "User not found")})
    List<Notification> pullNotificationForUser(@JsonRpcParam(value = "username") String username);
}

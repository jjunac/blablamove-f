package fr.unice.polytech.al.teamf.usernotifier.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.usernotifier.PullNotifications;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.entities.User;
import fr.unice.polytech.al.teamf.usernotifier.exceptions.UnknownUserException;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AutoJsonRpcServiceImpl
public class NotificationServiceImpl implements NotificationService {
    
    
    private UserRepository userRepository;
    
    private PullNotifications pullNotifications;
    
    @Autowired
    public NotificationServiceImpl(UserRepository userRepository, PullNotifications pullNotifications) {
        this.userRepository = userRepository;
        this.pullNotifications = pullNotifications;
    }
    
    @Override
    public List<Notification> pullNotificationForUser(String username) {
        User user = userRepository.findByName(username).stream().findFirst().orElseThrow(() -> new UnknownUserException(username));
        return pullNotifications.pullNotificationForUser(user);
    }
}
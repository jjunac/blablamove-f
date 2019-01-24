package fr.unice.polytech.al.teamf.usernotifier.webservices;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import fr.unice.polytech.al.teamf.usernotifier.PullNotifications;
import fr.unice.polytech.al.teamf.usernotifier.entities.Notification;
import fr.unice.polytech.al.teamf.usernotifier.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AutoJsonRpcServiceImpl
public class NotificationServiceImpl implements NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    
    private UserRepository userRepository;
    
    private PullNotifications pullNotifications;
    
    @Autowired
    public NotificationServiceImpl(UserRepository userRepository, PullNotifications pullNotifications) {
        this.userRepository = userRepository;
        this.pullNotifications = pullNotifications;
    }
    
    @Override
    public List<Notification> pullNotificationForUser(String username) {
        return pullNotifications.pullNotificationForUser(userRepository.findByName(username).get(0));
    }
}
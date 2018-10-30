package fr.unice.polytech.al.teamf.repositories;

import fr.unice.polytech.al.teamf.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);
}

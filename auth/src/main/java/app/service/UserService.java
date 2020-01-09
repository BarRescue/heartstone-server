package app.service;

import app.entity.User;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    public User createOrUpdate(User user) {
        return userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.getOne(id);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}
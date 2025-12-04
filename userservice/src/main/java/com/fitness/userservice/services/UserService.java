package com.fitness.userservice.services;


import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private  final UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            User existingUser = userRepository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();

            userResponse.setId(existingUser.getId());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getUpdatedAt());

            return  userResponse;
//            throw  new RuntimeException("email already exist");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse();

        userResponse.setId(savedUser.getId());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setKeycloakId(savedUser.getKeycloakId());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;
    }


    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not found"));
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return  userResponse;
    }

    public Boolean existsByUserId(String userId) {
        return  userRepository.existsByKeycloakId(userId);
    }
}

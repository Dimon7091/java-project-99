package hexlet.code.app.service;

import hexlet.code.app.dto.userDTO.UserCreateDTO;
import hexlet.code.app.dto.userDTO.UserDTO;
import hexlet.code.app.dto.userDTO.UserFullUpdateDTO;
import hexlet.code.app.dto.userDTO.UserPartialUpdateDTO;
import hexlet.code.app.exception.EmailAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    // === Create ===
    public UserDTO create(UserCreateDTO userData) {
        if (userRepository.existsByEmail(userData.email())) {
            throw new EmailAlreadyExistsException("email " + userData.email() + " уже существует");
        }
        var user = mapper.toEntity(userData);
        var savedUser = userRepository.save(user);
        return mapper.toDto(savedUser);
    }

    // === Read ===
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user -> mapper.toDto(user))
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + id + " не существует"));
        return mapper.toDto(user);
    }

    public UserDTO findByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("Пользователь с email " + email + " не существует");
        }
        return mapper.toDto(userRepository.findByEmail(email).get());
    }

    // === Update ===
    public UserDTO fullUpdate(Long id, UserFullUpdateDTO userData) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + id + " не существует"));

        if (!user.getEmail().equals(userData.email()) &&
                userRepository.existsByEmail(userData.email())) {
            throw new EmailAlreadyExistsException("email " + userData.email() + " уже занят");
        }

        mapper.fullUpdate(userData, user);
        userRepository.save(user);
        return mapper.toDto(user);
    }

    public UserDTO partialUpdate(Long id, UserPartialUpdateDTO userData) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + id + " не существует"));

        // Обрабатываем email (если он передан в запросе)
        if (userData.email() != null && userData.email().isPresent()) {
            String newEmail = userData.email().get();

            if (!user.getEmail().equals(newEmail) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException(
                        "email " + newEmail + " уже занят"
                );
            }
        }
        mapper.partialUpdate(userData, user);
        return mapper.toDto(user);
    }

    // === Delete ===
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EmailAlreadyExistsException("Пользователь с id " + id + " не существует");
        }
        userRepository.deleteById(id);
    }
}

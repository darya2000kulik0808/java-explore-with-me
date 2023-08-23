package ru.practicum.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.errorHandler.exceptions.ConflictException;
import ru.practicum.errorHandler.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.models.User;
import ru.practicum.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        User userToAdd = UserMapper.toUser(newUserRequest);

        User userByEmailAndName = userRepository.findByEmailAndName(userToAdd.getEmail(), userToAdd.getName());
        if (userByEmailAndName != null) {
            throw new ConflictException(
                    String.format("Нарушение уникальности пользователя. Пользователь \"%s\" уже существует.",
                            newUserRequest.getEmail()));
        }
        User userSaved = userRepository.save(userToAdd);
        log.debug("Успешно сохранили пользователя...");
        return UserMapper.toUserDto(userSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<User> users;
        if (ids.size() != 0) {
            users = userRepository.findAllByIdIn(ids, page).getContent();
        } else {
            users = userRepository.findAll(page).getContent();
        }

        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("User with id=%d was not found", userId)
                ));
        userRepository.deleteById(userId);
    }
}

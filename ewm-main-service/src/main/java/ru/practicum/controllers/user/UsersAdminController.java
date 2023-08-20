package ru.practicum.controllers.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.services.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.errorHandler.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.errorHandler.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/users")
public class UsersAdminController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return service.addUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                             @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                             @RequestParam(defaultValue = "0") Integer from,
                             @Positive(message = SIZE_ERROR_MESSAGE)
                             @RequestParam(defaultValue = "10") Integer size) {
        return service.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
    }
}

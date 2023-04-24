package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceTest {
    private final UserService userService;
    private static UserDto user1;
    private static UserDto user2;
    private static UserDtoUpdate updateUser1;

    @BeforeAll
    public static void setUp() {
        user1 = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
        user2 = UserDto.builder()
                .name("test name 2")
                .email("test2@test.ru")
                .build();
    }

    @Test
    public void createAndGetUser() {
        //when
        UserDtoResponse savedUser = userService.createUser(user1);
        UserDtoResponse findUser = userService.getUserById(1L);
        //then
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
    }

    @Test
    public void createUserWithDuplicateEmail() {
        //given
        userService.createUser(user1);
        assertThatThrownBy(
                //when
                () -> userService.createUser(user1))
                //then
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void getNotExistUserById() {
        assertThatThrownBy(
                //when
                () -> userService.getUserById(2L))
                //then
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getEmptyUsersList() {
        //when
        List<UserDtoResponse> users = userService.getUserRepository();
        //then
        assertThat(users).isEmpty();
    }

    @Test
    public void getUsersList() {
        //when
        UserDtoResponse savedUser1 = userService.createUser(user1);
        UserDtoResponse savedUser2 = userService.createUser(user2);
        List<UserDtoResponse> findUsers = userService.getUserRepository();
        //then
        assertThat(findUsers).element(0).usingRecursiveComparison().isEqualTo(savedUser1);
        assertThat(findUsers).element(1).usingRecursiveComparison().isEqualTo(savedUser2);
    }

    @Test
    public void updateUser() {
        //given
        updateUser1 = UserDtoUpdate.builder()
                .name("update name")
                .email("update-email@test.ru")
                .build();
        //when
        userService.createUser(user1);
        userService.updateUser(updateUser1, 1L);
        UserDtoResponse updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updateUser1.getEmail());
    }

    @Test
    public void updateUserEmail() {
        //given
        updateUser1 = UserDtoUpdate.builder()
                .email("update-email@test.ru")
                .build();
        //when
        userService.createUser(user1);
        userService.updateUser(updateUser1, 1L);
        UserDtoResponse updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(user1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updatedUser1.getEmail());
    }

    @Test
    public void updateUserName() {
        //given
        updateUser1 = UserDtoUpdate.builder()
                .name("update name")
                .build();
        //when
        userService.createUser(user1);
        userService.updateUser(updateUser1, 1L);
        UserDtoResponse updatedUser1 = userService.getUserById(1L);
        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateUserDuplicateEmail() {
        //given
        updateUser1 = UserDtoUpdate.builder()
                .email(user1.getEmail())
                .build();
        //when
        userService.createUser(user1);
        userService.createUser(user2);
        assertThatThrownBy(
                () -> userService.updateUser(updateUser1, 2L))
                //then
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void deleteUserById() {
        //given
        UserDtoResponse savedUser = userService.createUser(user1);
        //when
        userService.deleteUser(savedUser.getId());
        //then
        assertThatThrownBy(() -> userService.getUserById(savedUser.getId())).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void deleteUserByNotExistId() {
        assertThatThrownBy(
                //when
                () -> userService.deleteUser(1L)
        )
                //then
                .isInstanceOf(ResponseStatusException.class);
    }
}
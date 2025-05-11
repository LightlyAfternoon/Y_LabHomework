package org.example.command;

import org.example.CurrentUser;
import org.example.command.user.User;
import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@DisplayName("Tests of class with methods for console input")
class UserTest {
    UserService userService;
    User user;
    HttpRequestsClass httpRequestsClass;
    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        UserEntity userEntity = new UserEntity(1);

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        userDTO = new UserDTO.UserBuilder(CurrentUser.currentUser.getEmail(), CurrentUser.currentUser.getPassword(), CurrentUser.currentUser.getName()).
                id(CurrentUser.currentUser.getId()).role(CurrentUser.currentUser.getRole()).isBlocked(CurrentUser.currentUser.isBlocked()).build();

        user = new User(httpRequestsClass);
    }

    @DisplayName("Test of the method for getting logged in user by email and password")
    @Test
    void getLoggedInUserRoleTest() {
        String request = "t\nt";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t", "t")).thenReturn(userDTO);
        Mockito.when(httpRequestsClass.getLoggedInUser("t", "t")).thenReturn(new UserDTO.UserBuilder("t", "t", "t").id(1).build());

        Assertions.assertEquals(UserRole.USER, user.getLoggedInUserRole());

        request = "t2\nt";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t2", "t")).thenReturn(null);
        Mockito.when(httpRequestsClass.getLoggedInUser("t2", "t")).thenReturn(null);

        Assertions.assertNull(user.getLoggedInUserRole());

        request = "t\nt2";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t", "t2")).thenReturn(null);
        Mockito.when(httpRequestsClass.getLoggedInUser("t", "t2")).thenReturn(null);

        Assertions.assertNull(user.getLoggedInUserRole());
    }

    @DisplayName("Test of the method for getting all users")
    @Test
    void getAllUsersTest() {
        List<UserDTO> userDTOS = List.of(userDTO);
        StringBuilder output = new StringBuilder();

        for (UserDTO dto : userDTOS) {
            output.append(dto).append("\n");
        }

        Mockito.when(userService.findAll()).thenReturn(userDTOS);
        Mockito.when(httpRequestsClass.getAllUsers()).thenReturn(userDTOS);

        Assertions.assertEquals(output.toString(), user.getAllUsers());
    }

    @DisplayName("Test of the method for getting registered user")
    @Test
    void getRegisteredUserTest() {
        String request = "t\nt\nt";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setEmail("t");
        userDTO2.setPassword("t");
        userDTO2.setName("t");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        Mockito.when(userService.add(userDTO2)).thenReturn(null);
        Mockito.when(httpRequestsClass.getRegisteredUser("t", "t", "t")).thenReturn(null);

        Assertions.assertNull(user.getRegisteredUser());

        request = "t2\nt\nt2";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        userDTO2 = new UserDTO();
        userDTO2.setEmail("t2");
        userDTO2.setPassword("t");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        Mockito.when(userService.add(userDTO2)).thenReturn(userDTO2);
        Mockito.when(httpRequestsClass.getRegisteredUser("t2", "t", "t2")).thenReturn(userDTO2);

        Assertions.assertEquals(userDTO2, user.getRegisteredUser());
    }

    @DisplayName("Test of the method for deleting account of current user")
    @Test
    void deleteAccountTest() {
        Mockito.when(userService.delete(CurrentUser.currentUser.getId())).thenReturn(true);
        Mockito.when(httpRequestsClass.deleteAccount(CurrentUser.currentUser.getId())).thenReturn(true);

        Assertions.assertTrue(user.accountDeleted());
    }
}
package org.example.service;

import org.example.config.MyTestConfig;
import org.example.controller.dto.UserDTO;
import org.example.controller.mapper.UserDTOMapper;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@DisplayName("Tests of user service methods")
class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Spy
    UserDTOMapper userDTOMapper = Mappers.getMapper(UserDTOMapper.class);

    @BeforeAll
    static void beforeAll() {
        MyTestConfig.setConfig();
    }

    @DisplayName("Test of the method for adding user")
    @Test
    void addTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userRepository.findByEmail("t")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        userDTO = userService.add(userDTO);

        Assertions.assertNotEquals(0, userDTO.getId());

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t");
        userDTO2.setPassword("t");
        userDTO2.setName("t");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        Mockito.when(userRepository.findByEmail("t")).thenReturn(user);

        userDTO2 = userService.add(userDTO2);

        Assertions.assertNull(userDTO2);

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t");
        userDTO3.setPassword("t2");
        userDTO3.setName("t2");
        userDTO3.setRole(UserRole.USER);
        userDTO3.setBlocked(true);

        Mockito.when(userRepository.findByEmail("t")).thenReturn(user);

        userDTO3 = userService.add(userDTO3);

        Assertions.assertNull(userDTO3);
    }

    @DisplayName("Test of the method for finding user by id")
    @Test
    void findByIdTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userRepository.findByEmail("t")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        userDTO = userService.add(userDTO);

        Mockito.when(userRepository.findById(1)).thenReturn(user);
        Assertions.assertEquals(userService.findById(userDTO.getId()), userDTO);

        userDTO.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userService.findById(userDTO.getId()), userDTO);

        Assertions.assertNull(userService.findById(50));
    }

    @DisplayName("Test of the method for finding all users")
    @Test
    void findAllTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(true);

        UserEntity user2 = new UserEntity.UserBuilder("t2", "t2", "t2").id(2).isBlocked(true).build();

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t3");
        userDTO3.setPassword("t2");
        userDTO3.setName("t3");
        userDTO3.setRole(UserRole.ADMIN);
        userDTO3.setBlocked(false);

        UserEntity user3 = new UserEntity.UserBuilder("t3", "t2", "t3").id(3).role(UserRole.ADMIN).build();

        List<UserDTO> userEntities = List.of(userDTO, userDTO2, userDTO3);

        List<UserDTO> userEntitiesReturned;

        Mockito.when(userRepository.findByEmail("t")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        Mockito.when(userRepository.findByEmail("t2")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO2))).thenReturn(user2);

        Mockito.when(userRepository.findByEmail("t3")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO3))).thenReturn(user3);

        userService.add(userDTO);
        userService.add(userDTO2);
        userService.add(userDTO3);

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, user2, user3));

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        UserDTO userDTO4 = new UserDTO();

        userDTO4.setEmail("t4");
        userDTO4.setPassword("t2");
        userDTO4.setName("t4");
        userDTO4.setRole(UserRole.ADMIN);
        userDTO4.setBlocked(false);

        UserEntity user4 = new UserEntity.UserBuilder("t4", "t2", "t4").id(4).role(UserRole.ADMIN).build();

        userEntities = List.of(userDTO, userDTO2, userDTO3, userDTO4);

        Mockito.when(userRepository.findByEmail("t4")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO4))).thenReturn(user4);

        userService.add(userDTO4);

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, user2, user3, user4));

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userDTO.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);
    }

    @DisplayName("Test of the method for updating user")
    @Test
    void updateTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userRepository.findByEmail("t")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        userDTO = userService.add(userDTO);

        UserDTO userDTO2 = new UserDTO(userDTO.getId());

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        UserEntity user2 = new UserEntity.UserBuilder("t2", "t2", "t2").id(userDTO.getId()).build();

        Mockito.when(userRepository.findByEmail("t2")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO2))).thenReturn(user2);

        userDTO2 = userService.update(userDTO2, userDTO2.getId());

        Mockito.when(userRepository.findById(userDTO.getId())).thenReturn(user2);

        Assertions.assertEquals(userDTO2, userService.findById(userDTO.getId()));

        userDTO2.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userDTO2, userService.findById(userDTO.getId()));
    }

    @DisplayName("Test of the method for deleting user")
    @Test
    void deleteTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(true);

        UserEntity user2 = new UserEntity.UserBuilder("t2", "t2", "t2").id(2).isBlocked(true).build();

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t3");
        userDTO3.setPassword("t2");
        userDTO3.setName("t3");
        userDTO3.setRole(UserRole.ADMIN);
        userDTO3.setBlocked(false);

        UserEntity user3 = new UserEntity.UserBuilder("t3", "t2", "t3").id(3).role(UserRole.ADMIN).build();

        List<UserDTO> userEntities = List.of(userDTO, userDTO2, userDTO3);

        Mockito.when(userRepository.findByEmail("t")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        Mockito.when(userRepository.findByEmail("t2")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO2))).thenReturn(user2);

        Mockito.when(userRepository.findByEmail("t3")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO3))).thenReturn(user3);

        userDTO = userService.add(userDTO);
        userDTO2 = userService.add(userDTO2);
        userDTO3 = userService.add(userDTO3);

        List<UserDTO> userEntitiesReturned;

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, user2, user3));

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        Mockito.doNothing().when(userRepository).delete(null);
        Mockito.when(userRepository.findById(userDTO.getId())).thenReturn(null);

        Assertions.assertTrue(userService.delete(userDTO.getId()));

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user2, user3));

        userEntitiesReturned = userService.findAll();

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);

        userEntities = List.of(userDTO2, userDTO3);

        Assertions.assertEquals(userEntities, userEntitiesReturned);
    }

    @DisplayName("Test of the method for finding user by email and password")
    @Test
    void findUserByEmailAndPasswordTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("te");
        userDTO.setPassword("tp");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserEntity user = new UserEntity.UserBuilder("te", "tp", "t").id(1).build();

        Mockito.when(userRepository.findByEmail("te")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        userDTO = userService.add(userDTO);

        Mockito.when(userRepository.findByEmailAndPassword("te", "tp")).thenReturn(user);

        Assertions.assertEquals(userService.findUserByEmailAndPassword("te", "tp"), userDTO);

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("te2");
        userDTO2.setPassword("tp2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        UserEntity user2 = new UserEntity.UserBuilder("te2", "tp2", "t2").id(2).isBlocked(true).build();

        Mockito.when(userRepository.findByEmail("te2")).thenReturn(null);
        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO2))).thenReturn(user2);

        userDTO2 = userService.add(userDTO2);

        Mockito.when(userRepository.findByEmailAndPassword("te2", "tp2")).thenReturn(user2);
        Mockito.when(userRepository.findByEmailAndPassword("te", "tp2")).thenReturn(null);
        Mockito.when(userRepository.findByEmailAndPassword("te2", "tp")).thenReturn(null);
        Mockito.when(userRepository.findByEmailAndPassword("tE2", "tp2")).thenReturn(null);
        Mockito.when(userRepository.findByEmailAndPassword("tE2", "tP2")).thenReturn(null);

        Assertions.assertEquals(userService.findUserByEmailAndPassword("te2", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("te", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("te2", "tp"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("tE2", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("tE2", "tP2"), userDTO2);
    }
}
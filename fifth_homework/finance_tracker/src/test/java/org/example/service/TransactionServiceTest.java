package org.example.service;

import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.dto.UserDTO;
import org.example.controller.mapper.TransactionDTOMapper;
import org.example.controller.mapper.UserDTOMapper;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.TransactionServiceImpl;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@SpringBootTest
@DisplayName("Tests of transaction service methods")
class TransactionServiceTest {
    TransactionCategoryEntity category;
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    TransactionServiceImpl transactionService;
    @Mock
    TransactionRepository transactionRepository;
    @Spy
    TransactionDTOMapper transactionDTOMapper = Mappers.getMapper(TransactionDTOMapper.class);
    @Spy
    UserDTOMapper userDTOMapper = Mappers.getMapper(UserDTOMapper.class);

    @BeforeAll
    static void beforeAll() {
        MyTestConfig.setConfig();
    }

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity(1);

        user.setEmail("t");
        user.setPassword("t");
        user.setName("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        CurrentUser.currentUser = user;

        category = new TransactionCategoryEntity(1, null);

        category.setName("t");
    }

    @DisplayName("Test of the method for adding transaction")
    @Test
    void addTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);

        TransactionDTO savedTransactionDTO = transactionService.add(transactionDTO);

        Assertions.assertNotEquals(0, savedTransactionDTO.getId());
        Assertions.assertEquals(transactionDTO, savedTransactionDTO);

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(10.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.0), CurrentUser.currentUser.getId()).
                id(2).categoryId(category.getId()).date(date).description("t2").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO2))).thenReturn(transaction2);

        transactionDTO2 = transactionService.add(transactionDTO2);

        Assertions.assertNotEquals(transactionDTO, transactionDTO2);
    }

    @DisplayName("Test of the method for finding transaction by id")
    @Test
    void findByIdTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);

        transactionDTO = transactionService.add(transactionDTO);

        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);

        Assertions.assertEquals(transactionService.findById(transactionDTO.getId()), transactionDTO);

        transactionDTO.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(transactionService.findById(transactionDTO.getId()), transactionDTO);

        Mockito.when(transactionRepository.findById(1)).thenReturn(null);

        Assertions.assertNull(transactionService.findById(10));
    }

    @DisplayName("Test of the method for finding all transactions")
    @Test
    void findAllTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20.0), CurrentUser.currentUser.getId()).
                id(2).categoryId(category.getId()).date(date).description("t2").build();

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionEntity transaction3 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(30.3), CurrentUser.currentUser.getId()).
                id(3).categoryId(category.getId()).date(date).description("t3").build();

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        List<TransactionDTO> transactionEntitiesReturned;

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO2))).thenReturn(transaction2);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO3))).thenReturn(transaction3);

        transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);

        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(transaction, transaction2, transaction3));

        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        date = new Date(System.currentTimeMillis());

        transactionDTO4.setSum(BigDecimal.valueOf(10.10));
        transactionDTO4.setCategoryId(category.getId());
        transactionDTO4.setDate(date);
        transactionDTO4.setDescription("t4");

        TransactionEntity transaction4 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(4).categoryId(category.getId()).date(date).description("t4").build();

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO4))).thenReturn(transaction4);

        transactionService.add(transactionDTO4);

        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(transaction, transaction2, transaction3, transaction4));

        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionDTO.setDescription("t5");

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all transactions by user id")
    @Test
    void findAllByUserIdTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20.0), CurrentUser.currentUser.getId()).
                id(2).categoryId(category.getId()).date(date).description("t2").build();

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionEntity transaction3 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(30.3), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t3").build();

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        List<TransactionDTO> transactionEntitiesReturned;

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO2))).thenReturn(transaction2);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO3))).thenReturn(transaction3);

        transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);

        Mockito.when(transactionRepository.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transaction, transaction2, transaction3));

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        date = new Date(System.currentTimeMillis());

        transactionDTO4.setSum(BigDecimal.valueOf(10.10));
        transactionDTO4.setCategoryId(category.getId());
        transactionDTO4.setDate(date);
        transactionDTO4.setDescription("t4");

        TransactionEntity transaction4 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t4").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO4))).thenReturn(transaction4);

        transactionDTO4 = transactionService.add(transactionDTO4);

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        Mockito.when(transactionRepository.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transaction, transaction2, transaction3, transaction4));

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t2");
        userDTO.setPassword("t2");
        userDTO.setName("t2");

        UserEntity user = new UserEntity.UserBuilder("t2", "t2", "t2").id(2).build();

        Mockito.when(userRepository.save(userDTOMapper.mapToEntity(userDTO))).thenReturn(user);

        userDTO = userService.add(userDTO);

        TransactionDTO transactionDTO5 = new TransactionDTO(userDTO.getId());

        transactionDTO5.setSum(BigDecimal.valueOf(10.10));
        transactionDTO5.setCategoryId(category.getId());
        transactionDTO5.setDate(date);
        transactionDTO5.setDescription("t5");

        TransactionEntity transaction5 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), userDTO.getId()).
                id(1).categoryId(category.getId()).date(date).description("t5").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO5))).thenReturn(transaction5);

        transactionDTO5 = transactionService.add(transactionDTO5);

        Mockito.when(transactionRepository.findAllByUserId(userDTO.getId())).thenReturn(List.of(transaction5));

        List<TransactionDTO> transactionEntities2 = List.of(transactionDTO5);

        List<TransactionDTO> transactionEntitiesReturned2 = transactionService.findAllByUserId(userDTO.getId());

        Assertions.assertEquals(transactionEntities2, transactionEntitiesReturned2);

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO.setSum(BigDecimal.valueOf(21.30));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(21.30), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);

        transactionDTO = transactionService.add(transactionDTO);

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for updating transaction")
    @Test
    void updateTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);

        transactionDTO = transactionService.add(transactionDTO);

        TransactionDTO transactionDTO2 = new TransactionDTO(transactionDTO.getId(), CurrentUser.currentUser.getId());

        date = new Date(System.currentTimeMillis());

        transactionDTO2.setSum(BigDecimal.valueOf(1.23));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(1.23), CurrentUser.currentUser.getId()).
                id(transactionDTO.getId()).categoryId(category.getId()).date(date).description("t2").build();

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO2))).thenReturn(transaction2);

        transactionDTO2 = transactionService.update(transactionDTO2, transactionDTO2.getId());

        Mockito.when(transactionRepository.findById(transactionDTO.getId())).thenReturn(transaction2);

        Assertions.assertEquals(transactionService.findById(transactionDTO.getId()), transactionDTO2);

        transactionDTO2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(transactionService.findById(transactionDTO.getId()), transactionDTO2);
    }

    @DisplayName("Test of the method for deleting transaction")
    @Test
    void deleteTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10.10), CurrentUser.currentUser.getId()).
                id(1).categoryId(category.getId()).date(date).description("t").build();

        TransactionDTO transactionDTO2 = new TransactionDTO(transactionDTO.getId(), CurrentUser.currentUser.getId());

        date = new Date(System.currentTimeMillis());

        transactionDTO2.setSum(BigDecimal.valueOf(1.23));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(1.23), CurrentUser.currentUser.getId()).
                id(transactionDTO.getId()).categoryId(category.getId()).date(date).description("t2").build();

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionEntity transaction3 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(30.3), CurrentUser.currentUser.getId()).
                id(transactionDTO.getId()).categoryId(category.getId()).date(date).description("t3").build();

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO))).thenReturn(transaction);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO2))).thenReturn(transaction2);
        Mockito.when(transactionRepository.save(transactionDTOMapper.mapToEntity(transactionDTO3))).thenReturn(transaction3);

        transactionDTO = transactionService.add(transactionDTO);
        transactionDTO2 = transactionService.add(transactionDTO2);
        transactionDTO3 = transactionService.add(transactionDTO3);

        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(transaction, transaction2, transaction3));

        List<TransactionDTO> transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        Mockito.doNothing().when(transactionRepository).delete((TransactionEntity) null);
        Mockito.when(transactionRepository.findById(transactionDTO.getId())).thenReturn(null);

        Assertions.assertTrue(transactionService.delete(transactionDTO.getId()));

        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(transaction2, transaction3));

        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionDTO2, transactionDTO3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}
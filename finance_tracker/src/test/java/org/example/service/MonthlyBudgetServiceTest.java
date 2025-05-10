package org.example.service;

import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.mapper.MonthlyBudgetDTOMapper;
import org.example.model.MonthlyBudgetEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.MonthlyBudgetRepository;
import org.example.service.impl.MonthlyBudgetServiceImpl;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@DisplayName("Tests of monthly budget service methods")
class MonthlyBudgetServiceTest {
    @InjectMocks
    MonthlyBudgetServiceImpl monthlyBudgetService;
    @Mock
    MonthlyBudgetRepository monthlyBudgetRepository;
    @Spy
    MonthlyBudgetDTOMapper budgetDTOMapper = Mappers.getMapper(MonthlyBudgetDTOMapper.class);

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
    }

    @DisplayName("Test of the method for adding monthly budget")
    @Test
    void addTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).id(1).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);

        MonthlyBudgetDTO savedMonthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Assertions.assertNotEquals(0, savedMonthlyBudgetDTO.getId());
        Assertions.assertEquals(monthlyBudgetDTO, savedMonthlyBudgetDTO);

        Date date2 = Date.valueOf("2000-02-01");
        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(10.0));

        MonthlyBudgetEntity budget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).id(2).date(date2).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO2))).thenReturn(budget2);

        monthlyBudgetDTO2 = monthlyBudgetService.add(monthlyBudgetDTO2);

        Assertions.assertNotEquals(monthlyBudgetDTO, monthlyBudgetDTO2);
    }

    @DisplayName("Test of the method for finding monthly budget by id")
    @Test
    void findByIdTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).id(1).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Mockito.when(monthlyBudgetRepository.findById(monthlyBudgetDTO.getId())).thenReturn(budget);

        Assertions.assertEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO);

        Assertions.assertNull(monthlyBudgetService.findById(20));
    }

    @DisplayName("Test of the method for finding monthly budget by date and user id")
    @Test
    void findByDateAndUserIdTest() {
        Date date = Date.valueOf("2000-01-01");
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Date date2 = Date.valueOf("2000-02-01");
        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.5));

        MonthlyBudgetEntity budget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(1.5)).
                id(2).date(date2).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO2))).thenReturn(budget2);

        monthlyBudgetDTO2 = monthlyBudgetService.add(monthlyBudgetDTO2);

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(date, CurrentUser.currentUser.getId())).thenReturn(budget);

        Assertions.assertEquals(monthlyBudgetService.findByDateAndUserId(date, CurrentUser.currentUser.getId()), monthlyBudgetDTO);

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(date2, CurrentUser.currentUser.getId())).thenReturn(budget2);

        Assertions.assertEquals(monthlyBudgetService.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetDTO2);
        Assertions.assertNotEquals(monthlyBudgetService.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetDTO);

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(Date.valueOf("2001-01-01"), CurrentUser.currentUser.getId())).thenReturn(null);

        Assertions.assertNull(monthlyBudgetService.findByDateAndUserId(Date.valueOf("2001-01-01"), CurrentUser.currentUser.getId()));
    }

    @DisplayName("Test of the method for finding all monthly budgets")
    @Test
    void findAllTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        Date date2 = new Date(System.currentTimeMillis() + 2_678_400_000L);
        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(20.0));

        MonthlyBudgetEntity budget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(20.0)).
                id(2).date(date2).build();

        MonthlyBudgetDTO monthlyBudgetDTO3 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO3.setSum(BigDecimal.valueOf(30.3));

        MonthlyBudgetEntity budget3 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(30.3)).
                id(3).date(date).build();

        List<MonthlyBudgetDTO> transactionEntities = List.of(monthlyBudgetDTO, monthlyBudgetDTO2);
        List<MonthlyBudgetDTO> transactionEntitiesReturned;

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);
        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO2))).thenReturn(budget2);
        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO3))).thenReturn(budget3);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);
        monthlyBudgetDTO2 = monthlyBudgetService.add(monthlyBudgetDTO2);
        monthlyBudgetDTO3 = monthlyBudgetService.add(monthlyBudgetDTO3);

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of(budget, budget2));

        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        Date date3 = new Date(date2.getTime() + 2_678_400_000L);
        MonthlyBudgetDTO monthlyBudgetDTO4 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date3);

        monthlyBudgetDTO4.setSum(BigDecimal.valueOf(10.0));

        MonthlyBudgetEntity budget4 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.0)).
                id(4).date(date3).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO4))).thenReturn(budget4);

        monthlyBudgetDTO4 = monthlyBudgetService.add(monthlyBudgetDTO4);

        transactionEntities = List.of(monthlyBudgetDTO, monthlyBudgetDTO2, monthlyBudgetDTO4);

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of(budget, budget2, budget4));

        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(1.1));

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for updating monthly budget")
    @Test
    void updateTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).id(1).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(monthlyBudgetDTO.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetEntity budget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(1.23)).
                id(monthlyBudgetDTO.getId()).date(date).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO2))).thenReturn(budget2);

        monthlyBudgetDTO2 = monthlyBudgetService.update(monthlyBudgetDTO2, monthlyBudgetDTO.getId());

        Mockito.when(monthlyBudgetRepository.findById(monthlyBudgetDTO.getId())).thenReturn(budget2);

        Assertions.assertEquals(monthlyBudgetDTO2, monthlyBudgetService.findById(monthlyBudgetDTO.getId()));

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(monthlyBudgetDTO2, monthlyBudgetService.findById(monthlyBudgetDTO.getId()));
    }

    @DisplayName("Test of the method for deleting monthly budget")
    @Test
    void deleteTest() {
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.10)).id(1).build();

        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(monthlyBudgetDTO.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetEntity budget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(1.23)).
                id(2).date(date).build();

        MonthlyBudgetDTO monthlyBudgetDTO3 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());

        monthlyBudgetDTO3.setSum(BigDecimal.valueOf(30.3));

        MonthlyBudgetEntity budget3 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(30.3)).
                id(3).build();

        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO))).thenReturn(budget);
        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO2))).thenReturn(budget2);
        Mockito.when(monthlyBudgetRepository.save(budgetDTOMapper.mapToEntity(monthlyBudgetDTO3))).thenReturn(budget3);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);
        monthlyBudgetDTO2 = monthlyBudgetService.add(monthlyBudgetDTO2);
        monthlyBudgetDTO3 = monthlyBudgetService.add(monthlyBudgetDTO3);

        List<MonthlyBudgetDTO> transactionEntities = List.of(monthlyBudgetDTO);

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of(budget));

        List<MonthlyBudgetDTO> transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        Mockito.doNothing().when(monthlyBudgetRepository).delete(null);
        Mockito.when(monthlyBudgetRepository.findById(monthlyBudgetDTO.getId())).thenReturn(null);

        Assertions.assertTrue(monthlyBudgetService.delete(monthlyBudgetDTO.getId()));

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of());

        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = new ArrayList<>();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}
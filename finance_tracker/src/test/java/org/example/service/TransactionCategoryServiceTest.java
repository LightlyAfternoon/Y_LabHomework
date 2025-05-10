package org.example.service;

import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.mapper.TransactionCategoryDTOMapper;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.TransactionCategoryRepository;
import org.example.service.impl.TransactionCategoryServiceImpl;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@DisplayName("Tests of transaction category service methods")
class TransactionCategoryServiceTest {
    @InjectMocks
    TransactionCategoryServiceImpl categoryService;
    @Mock
    TransactionCategoryRepository categoryRepository;
    @Spy
    TransactionCategoryDTOMapper categoryDTOMapper = Mappers.getMapper(TransactionCategoryDTOMapper.class);

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

    @DisplayName("Test of the method for adding category")
    @Test
    void addTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);

        TransactionCategoryDTO savedCategoryDTO = categoryService.add(categoryDTO);

        Assertions.assertNotEquals(0, savedCategoryDTO.getId());
        Assertions.assertEquals(categoryDTO, savedCategoryDTO);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t2");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").id(2).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);

        categoryDTO2 = categoryService.add(categoryDTO2);

        Assertions.assertNotEquals(categoryDTO, categoryDTO2);
    }

    @DisplayName("Test of the method for adding goal")
    @Test
    void addGoalTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO.setName("t");
        categoryDTO.setNeededSum(BigDecimal.valueOf(10.0));

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.0)).userId(CurrentUser.currentUser.getId()).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);

        TransactionCategoryDTO savedCategoryDTO = categoryService.add(categoryDTO);

        Assertions.assertNotEquals(0, savedCategoryDTO.getId());
        Assertions.assertEquals(categoryDTO, savedCategoryDTO);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO2.setName("t2");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").id(2).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);

        categoryDTO2 = categoryService.add(categoryDTO2);

        Assertions.assertNotEquals(categoryDTO, categoryDTO2);
    }

    @DisplayName("Test of the method for finding category by id")
    @Test
    void findByIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);

        categoryDTO = categoryService.add(categoryDTO);

        Mockito.when(categoryRepository.findById(categoryDTO.getId())).thenReturn(category);

        Assertions.assertEquals(categoryService.findById(categoryDTO.getId()), categoryDTO);

        categoryDTO.setName("t0");

        Assertions.assertNotEquals(categoryService.findById(categoryDTO.getId()), categoryDTO);

        Mockito.when(categoryRepository.findById(10)).thenReturn(null);

        Assertions.assertNull(categoryService.findById(10));
    }

    @DisplayName("Test of the method for finding category by name")
    @Test
    void findByNameTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);

        categoryDTO = categoryService.add(categoryDTO);

        Mockito.when(categoryRepository.findByName("t")).thenReturn(category);

        Assertions.assertEquals(categoryDTO, categoryService.findByName("t"));

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t0");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t0").id(1).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);

        categoryDTO2 = categoryService.add(categoryDTO2);

        Mockito.when(categoryRepository.findByName("t0")).thenReturn(category2);
        Mockito.when(categoryRepository.findByName("t2")).thenReturn(null);

        Assertions.assertNotEquals(categoryService.findByName("t"), categoryDTO2);
        Assertions.assertEquals(categoryService.findByName("t0"), categoryDTO2);
        Assertions.assertNull(categoryService.findByName("t2"));
    }

    @DisplayName("Test of the method for finding all categories")
    @Test
    void findAllTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t2");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").id(2).build();

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryEntity category3 = new TransactionCategoryEntity.TransactionCategoryBuilder("t3").id(3).build();

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO3))).thenReturn(category3);

        categoryDTO = categoryService.add(categoryDTO);
        categoryDTO2 = categoryService.add(categoryDTO2);
        categoryDTO3 = categoryService.add(categoryDTO3);

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(category, category2, category3));

        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO();

        categoryDTO4.setName("t4");

        TransactionCategoryEntity category4 = new TransactionCategoryEntity.TransactionCategoryBuilder("t4").id(4).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO4))).thenReturn(category4);

        categoryDTO4 = categoryService.add(categoryDTO4);

        categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3, categoryDTO4);

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(category, category2, category3, category4));

        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryDTO.setName("t0");

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all categories and goals with user id")
    @Test
    void findCommonCategoriesOrGoalsByUserIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO2.setName("t2");
        categoryDTO2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").
                id(2).neededSum(BigDecimal.valueOf(20.0)).userId(CurrentUser.currentUser.getId()).build();

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryEntity category3 = new TransactionCategoryEntity.TransactionCategoryBuilder("t3").id(3).build();

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO4.setName("t4");
        categoryDTO4.setNeededSum(BigDecimal.valueOf(40.4));

        TransactionCategoryEntity category4 = new TransactionCategoryEntity.TransactionCategoryBuilder("t4").
                id(4).neededSum(BigDecimal.valueOf(40.4)).userId(CurrentUser.currentUser.getId()).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO3))).thenReturn(category3);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO4))).thenReturn(category4);

        categoryDTO = categoryService.add(categoryDTO);
        categoryDTO2 = categoryService.add(categoryDTO2);
        categoryDTO3 = categoryService.add(categoryDTO3);
        categoryDTO4 = categoryService.add(categoryDTO4);

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO2, categoryDTO4);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category2, category4));

        transactionCategoryEntitiesReturned = categoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all goals with user id")
    @Test
    void findAllGoalsWithUserIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO2.setName("t2");
        categoryDTO2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").
                id(2).neededSum(BigDecimal.valueOf(20.0)).userId(CurrentUser.currentUser.getId()).build();

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryEntity category3 = new TransactionCategoryEntity.TransactionCategoryBuilder("t3").id(3).build();

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO4.setName("t4");
        categoryDTO4.setNeededSum(BigDecimal.valueOf(40.4));

        TransactionCategoryEntity category4 = new TransactionCategoryEntity.TransactionCategoryBuilder("t4").
                id(4).neededSum(BigDecimal.valueOf(40.4)).userId(CurrentUser.currentUser.getId()).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO3))).thenReturn(category3);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO4))).thenReturn(category4);

        categoryDTO = categoryService.add(categoryDTO);
        categoryDTO2 = categoryService.add(categoryDTO2);
        categoryDTO3 = categoryService.add(categoryDTO3);
        categoryDTO4 = categoryService.add(categoryDTO4);

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO2, categoryDTO4);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        Mockito.when(categoryRepository.findAllGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category2, category4));

        transactionCategoryEntitiesReturned = categoryService.findAllGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for updating category")
    @Test
    void updateTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);

        categoryDTO = categoryService.add(categoryDTO);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(categoryDTO.getId(), null);

        categoryDTO2.setName("t2");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").id(categoryDTO.getId()).build();

        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);

        categoryDTO2 = categoryService.update(categoryDTO2, categoryDTO.getId());

        Mockito.when(categoryRepository.findById(categoryDTO.getId())).thenReturn(category2);

        Assertions.assertEquals(categoryDTO2, categoryService.findById(categoryDTO.getId()));

        categoryDTO2.setName("t0");

        Assertions.assertNotEquals(categoryDTO2, categoryService.findById(categoryDTO.getId()));
    }

    @DisplayName("Test of the method for deleting category")
    @Test
    void deleteTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryEntity category = new TransactionCategoryEntity.TransactionCategoryBuilder("t").id(1).build();

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t2");

        TransactionCategoryEntity category2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").id(2).build();

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryEntity category3 = new TransactionCategoryEntity.TransactionCategoryBuilder("t3").id(3).build();


        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO))).thenReturn(category);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO2))).thenReturn(category2);
        Mockito.when(categoryRepository.save(categoryDTOMapper.mapToEntity(categoryDTO3))).thenReturn(category3);

        categoryDTO = categoryService.add(categoryDTO);
        categoryDTO2 = categoryService.add(categoryDTO2);
        categoryDTO3 = categoryService.add(categoryDTO3);

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3);

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(category, category2, category3));

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        Mockito.when(categoryRepository.findById(categoryDTO.getId())).thenReturn(null);
        Mockito.doNothing().when(categoryRepository).delete(null);

        Assertions.assertTrue(categoryService.delete(categoryDTO.getId()));

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(category2, category3));

        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntities = List.of(categoryDTO2, categoryDTO3);

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }
}
package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.annotation.Loggable;
import org.example.model.TransactionCategoryEntity;
import org.example.repository.TransactionCategoryRepository;
import org.example.servlet.dto.TransactionCategoryDTO;
import org.example.servlet.mapper.TransactionCategoryDTOMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Loggable
public class TransactionCategoryService {
    TransactionCategoryRepository transactionCategoryRepository;
    TransactionCategoryDTOMapper transactionCategoryDTOMapper = TransactionCategoryDTOMapper.INSTANCE;

    public TransactionCategoryService(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    public TransactionCategoryDTO add(TransactionCategoryDTO transactionCategoryDTO) throws SQLException, LiquibaseException {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.add(transactionCategoryDTOMapper.mapToEntity(transactionCategoryDTO)));
    }

    public TransactionCategoryDTO findById(int id) throws SQLException, LiquibaseException {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findById(id));
    }

    public List<TransactionCategoryDTO> findAll() throws SQLException, LiquibaseException {
        return transactionCategoryRepository.findAll().stream().map(TransactionCategoryDTOMapper.INSTANCE::mapToDTO).toList();
    }

    public TransactionCategoryDTO update(TransactionCategoryDTO transactionCategoryDTO, int id) throws SQLException, LiquibaseException {
        TransactionCategoryDTO dto = new TransactionCategoryDTO.TransactionCategoryBuilder(transactionCategoryDTO.getName()).
                id(id).neededSum(transactionCategoryDTO.getNeededSum()).userId(transactionCategoryDTO.getUserId()).build();
        transactionCategoryRepository.update(transactionCategoryDTOMapper.mapToEntity(dto));

        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findById(id));
    }

    public boolean delete(int id) throws SQLException, LiquibaseException {
        TransactionCategoryEntity transactionCategory = transactionCategoryRepository.findById(id);

        return transactionCategoryRepository.delete(transactionCategory);
    }

    public List<TransactionCategoryDTO> findAllGoalsWithUserId(int userId) throws SQLException, LiquibaseException {
        return transactionCategoryRepository.findAllGoalsWithUserId(userId).stream().map(TransactionCategoryDTOMapper.INSTANCE::mapToDTO).toList();
    }

    public List<TransactionCategoryDTO> findCommonCategoriesOrGoalsWithUserId(int userId) throws SQLException, LiquibaseException {
        return transactionCategoryRepository.findCommonCategoriesOrGoalsWithUserId(userId).stream().map(TransactionCategoryDTOMapper.INSTANCE::mapToDTO).toList();
    }

    public TransactionCategoryDTO findByName(String name) throws SQLException, LiquibaseException {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findByName(name));
    }
}
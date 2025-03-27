package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.annotation.Loggable;
import org.example.model.TransactionEntity;
import org.example.repository.TransactionRepository;
import org.example.servlet.dto.TransactionDTO;
import org.example.servlet.mapper.TransactionDTOMapper;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Loggable
public class TransactionService {
    TransactionRepository transactionRepository;
    TransactionDTOMapper transactionDTOMapper = TransactionDTOMapper.INSTANCE;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionDTO add(TransactionDTO transactionDTO) throws SQLException, LiquibaseException {
        return transactionDTOMapper.mapToDTO(transactionRepository.add(transactionDTOMapper.mapToEntity(transactionDTO)));
    }

    public TransactionDTO findById(int id) throws SQLException, LiquibaseException {
        return transactionDTOMapper.mapToDTO(transactionRepository.findById(id));
    }

    public List<TransactionDTO> findAll() throws SQLException, LiquibaseException {
        List<TransactionEntity> transactionEntities = transactionRepository.findAll();
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        for (TransactionEntity transaction : transactionEntities) {
            transactionDTOS.add(transactionDTOMapper.mapToDTO(transaction));
        }

        return transactionDTOS;
    }

    public TransactionDTO update(TransactionDTO transactionDTO, int id) throws SQLException, LiquibaseException {
        TransactionDTO dto = new TransactionDTO.TransactionBuilder(transactionDTO.getSum(), transactionDTO.getUserId()).
                id(id).date(transactionDTO.getDate()).description(transactionDTO.getDescription()).categoryId(transactionDTO.getCategoryId()).build();
        transactionRepository.update(transactionDTOMapper.mapToEntity(dto));

        return transactionDTOMapper.mapToDTO(transactionRepository.findById(id));
    }

    public boolean delete(int id) throws SQLException, LiquibaseException {
        TransactionEntity transaction = transactionRepository.findById(id);

        return transactionRepository.delete(transaction);
    }

    public List<TransactionDTO> findAllWithUser(int userId) throws SQLException, LiquibaseException {
        List<TransactionEntity> transactionEntities = transactionRepository.findAllWithUser(userId);
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        for (TransactionEntity transaction : transactionEntities) {
            transactionDTOS.add(transactionDTOMapper.mapToDTO(transaction));
        }

        return transactionDTOS;
    }

    public List<TransactionDTO> findAllWithDateAndCategoryIdAndTypeAndUserId(Date date, int categoryId, String type, int userId) throws SQLException, LiquibaseException {
        List<TransactionEntity> transactionEntities = transactionRepository.findAllWithDateAndCategoryIdAndTypeAndUserId(date, categoryId, type, userId);
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        for (TransactionEntity transaction : transactionEntities) {
            transactionDTOS.add(transactionDTOMapper.mapToDTO(transaction));
        }

        return transactionDTOS;
    }
}
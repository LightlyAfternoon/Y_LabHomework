package org.example.service.impl;

import org.example.annotation.Loggable;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.mapper.TransactionDTOMapper;
import org.example.model.TransactionEntity;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.example.service.specification.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Loggable
@Service
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionDTOMapper transactionDTOMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionDTOMapper transactionDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDTOMapper = transactionDTOMapper;
    }

    public TransactionDTO add(TransactionDTO transactionDTO) {
        TransactionDTO dto = new TransactionDTO.TransactionBuilder(transactionDTO.getSum(), transactionDTO.getUserId()).
                date(transactionDTO.getDate()).description(transactionDTO.getDescription()).categoryId(transactionDTO.getCategoryId()).build();

        return transactionDTOMapper.mapToDTO(transactionRepository.save(transactionDTOMapper.mapToEntity(dto)));
    }

    public TransactionDTO findById(int id) {
        return transactionDTOMapper.mapToDTO(transactionRepository.findById(id));
    }

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream().map(transactionDTOMapper::mapToDTO).toList();
    }

    public TransactionDTO update(TransactionDTO transactionDTO, int id) {
        TransactionDTO dto = new TransactionDTO.TransactionBuilder(transactionDTO.getSum(), transactionDTO.getUserId()).
                id(id).date(transactionDTO.getDate()).description(transactionDTO.getDescription()).categoryId(transactionDTO.getCategoryId()).build();

        return transactionDTOMapper.mapToDTO(transactionRepository.save(transactionDTOMapper.mapToEntity(dto)));
    }

    public boolean delete(int id) {
        TransactionEntity transaction = transactionRepository.findById(id);
        transactionRepository.delete(transaction);
        transaction = transactionRepository.findById(id);

        return transaction == null;
    }

    public List<TransactionDTO> findAllByUserId(int userId) {
        return transactionRepository.findAllByUserId(userId).stream().map(transactionDTOMapper::mapToDTO).toList();
    }

    public List<TransactionDTO> findAllByDateAndCategoryIdAndTypeAndUserId(Date date, Integer categoryId, String type, int userId) {
        Specification<TransactionEntity> filters = TransactionSpecification.dateIs(date).and(TransactionSpecification.categoryIdIs(categoryId)).and(TransactionSpecification.sumTypeIs(type)).and(TransactionSpecification.userIdIs(userId));

        return transactionRepository.findAll(filters).stream().map(transactionDTOMapper::mapToDTO).toList();
    }
}
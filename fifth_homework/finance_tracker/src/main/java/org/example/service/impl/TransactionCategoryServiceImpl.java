package org.example.service.impl;

import org.example.annotation.Loggable;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.mapper.TransactionCategoryDTOMapper;
import org.example.model.TransactionCategoryEntity;
import org.example.repository.TransactionCategoryRepository;
import org.example.service.TransactionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Loggable
@Service
public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    TransactionCategoryRepository transactionCategoryRepository;
    TransactionCategoryDTOMapper transactionCategoryDTOMapper;

    @Autowired
    public TransactionCategoryServiceImpl(TransactionCategoryRepository transactionCategoryRepository, TransactionCategoryDTOMapper transactionCategoryDTOMapper) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionCategoryDTOMapper = transactionCategoryDTOMapper;
    }

    public TransactionCategoryDTO add(TransactionCategoryDTO transactionCategoryDTO) {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.save(transactionCategoryDTOMapper.mapToEntity(transactionCategoryDTO)));
    }

    public TransactionCategoryDTO findById(int id) {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findById(id));
    }

    public List<TransactionCategoryDTO> findAll() {
        return transactionCategoryRepository.findAll().stream().map(transactionCategoryDTOMapper::mapToDTO).toList();
    }

    public TransactionCategoryDTO update(TransactionCategoryDTO transactionCategoryDTO, int id) {
        TransactionCategoryDTO dto = new TransactionCategoryDTO.TransactionCategoryBuilder(transactionCategoryDTO.getName()).
                id(id).neededSum(transactionCategoryDTO.getNeededSum()).userId(transactionCategoryDTO.getUserId()).build();
        transactionCategoryRepository.save(transactionCategoryDTOMapper.mapToEntity(dto));

        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findById(id));
    }

    public boolean delete(int id) {
        TransactionCategoryEntity transactionCategory = transactionCategoryRepository.findById(id);
        transactionCategoryRepository.delete(transactionCategory);
        transactionCategory = transactionCategoryRepository.findById(id);

        return transactionCategory == null;
    }

    public List<TransactionCategoryDTO> findAllGoalsByUserId(int userId) {
        return transactionCategoryRepository.findAllGoalsByUserId(userId).stream().map(transactionCategoryDTOMapper::mapToDTO).toList();
    }

    public List<TransactionCategoryDTO> findCommonCategoriesOrGoalsByUserId(int userId) {
        return transactionCategoryRepository.findCommonCategoriesOrGoalsByUserId(userId).stream().map(transactionCategoryDTOMapper::mapToDTO).toList();
    }

    public TransactionCategoryDTO findByName(String name) {
        return transactionCategoryDTOMapper.mapToDTO(transactionCategoryRepository.findByName(name));
    }
}
package org.example.service;

import org.example.controller.dto.TransactionCategoryDTO;

import java.util.List;

public interface TransactionCategoryService {
    TransactionCategoryDTO add(TransactionCategoryDTO transactionCategoryDTO);

    TransactionCategoryDTO findById(int id);

    List<TransactionCategoryDTO> findAll();

    TransactionCategoryDTO update(TransactionCategoryDTO transactionCategoryDTO, int id);

    boolean delete(int id);

    List<TransactionCategoryDTO> findAllGoalsWithUserId(int userId) ;

    List<TransactionCategoryDTO> findCommonCategoriesOrGoalsWithUserId(int userId);

    TransactionCategoryDTO findByName(String name);
}
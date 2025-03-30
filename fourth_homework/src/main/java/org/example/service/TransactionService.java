package org.example.service;

import org.example.controller.dto.TransactionDTO;

import java.sql.Date;
import java.util.List;

public interface TransactionService {
    TransactionDTO add(TransactionDTO transactionDTO);

    TransactionDTO findById(int id);

    List<TransactionDTO> findAll();

    TransactionDTO update(TransactionDTO transactionDTO, int id);

    boolean delete(int id);

    List<TransactionDTO> findAllWithUser(int userId);

    List<TransactionDTO> findAllWithDateAndCategoryIdAndTypeAndUserId(Date date, int categoryId, String type, int userId);
}
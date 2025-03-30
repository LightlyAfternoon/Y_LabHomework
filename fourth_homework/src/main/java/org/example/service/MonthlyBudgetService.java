package org.example.service;

import org.example.controller.dto.MonthlyBudgetDTO;

import java.sql.Date;
import java.util.List;

public interface MonthlyBudgetService {
    MonthlyBudgetDTO add(MonthlyBudgetDTO monthlyBudgetDTO);

    MonthlyBudgetDTO findById(int id);

    List<MonthlyBudgetDTO> findAll();

    MonthlyBudgetDTO update(MonthlyBudgetDTO monthlyBudgetDTO, int id);

    boolean delete(int id);

    MonthlyBudgetDTO findByDateAndUserId(Date date, int userId);
}
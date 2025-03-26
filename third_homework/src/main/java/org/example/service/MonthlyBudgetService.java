package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.annotation.Loggable;
import org.example.model.MonthlyBudgetEntity;
import org.example.repository.MonthlyBudgetRepository;
import org.example.servlet.dto.MonthlyBudgetDTO;
import org.example.servlet.mapper.MonthlyBudgetDTOMapper;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Loggable
public class MonthlyBudgetService {
    MonthlyBudgetRepository monthlyBudgetRepository;
    MonthlyBudgetDTOMapper monthlyBudgetDTOMapper = MonthlyBudgetDTOMapper.INSTANCE;

    public MonthlyBudgetService(MonthlyBudgetRepository monthlyBudgetRepository) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
    }

    public MonthlyBudgetDTO add(MonthlyBudgetDTO monthlyBudgetDTO) throws SQLException, LiquibaseException {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.add(monthlyBudgetDTOMapper.mapToEntity(monthlyBudgetDTO)));
    }

    public MonthlyBudgetDTO findById(int id) throws SQLException, LiquibaseException {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findById(id));
    }

    public List<MonthlyBudgetDTO> findAll() throws SQLException, LiquibaseException {
        List<MonthlyBudgetEntity> monthlyBudgetEntities = monthlyBudgetRepository.findAll();
        List<MonthlyBudgetDTO> monthlyBudgetDTOS = new ArrayList<>();

        for (MonthlyBudgetEntity monthlyBudget : monthlyBudgetEntities) {
            monthlyBudgetDTOS.add(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget));
        }

        return monthlyBudgetDTOS;
    }

    public MonthlyBudgetDTO update(MonthlyBudgetDTO monthlyBudgetDTO, int id) throws SQLException, LiquibaseException {
        MonthlyBudgetDTO dto = new MonthlyBudgetDTO.MonthlyBudgetBuilder(monthlyBudgetDTO.getUserId(), monthlyBudgetDTO.getSum()).
                id(id).date(monthlyBudgetDTO.getDate()).build();
        monthlyBudgetRepository.update(monthlyBudgetDTOMapper.mapToEntity(dto));

        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findById(id));
    }

    public boolean delete(int id) throws SQLException, LiquibaseException {
        MonthlyBudgetEntity monthlyBudget = monthlyBudgetRepository.findById(id);

        return monthlyBudgetRepository.delete(monthlyBudget);
    }

    public MonthlyBudgetDTO findByDateAndUserId(Date date, int userId) throws SQLException, LiquibaseException {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findByDateAndUserId(date, userId));
    }
}
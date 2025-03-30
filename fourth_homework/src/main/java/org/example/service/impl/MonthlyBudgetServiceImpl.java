package org.example.service.impl;

import org.example.annotation.Loggable;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.mapper.MonthlyBudgetDTOMapper;
import org.example.model.MonthlyBudgetEntity;
import org.example.repository.MonthlyBudgetRepository;
import org.example.service.MonthlyBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Loggable
@Service
public class MonthlyBudgetServiceImpl implements MonthlyBudgetService {
    MonthlyBudgetRepository monthlyBudgetRepository;
    MonthlyBudgetDTOMapper monthlyBudgetDTOMapper;

    @Autowired
    public MonthlyBudgetServiceImpl(MonthlyBudgetRepository monthlyBudgetRepository, MonthlyBudgetDTOMapper monthlyBudgetDTOMapper) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.monthlyBudgetDTOMapper = monthlyBudgetDTOMapper;
    }

    public MonthlyBudgetDTO add(MonthlyBudgetDTO monthlyBudgetDTO) {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.save(monthlyBudgetDTOMapper.mapToEntity(monthlyBudgetDTO)));
    }

    public MonthlyBudgetDTO findById(int id) {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findById(id));
    }

    public List<MonthlyBudgetDTO> findAll() {
        List<MonthlyBudgetEntity> monthlyBudgetEntities = monthlyBudgetRepository.findAll();
        List<MonthlyBudgetDTO> monthlyBudgetDTOS = new ArrayList<>();

        for (MonthlyBudgetEntity monthlyBudget : monthlyBudgetEntities) {
            monthlyBudgetDTOS.add(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget));
        }

        return monthlyBudgetDTOS;
    }

    public MonthlyBudgetDTO update(MonthlyBudgetDTO monthlyBudgetDTO, int id) {
        MonthlyBudgetDTO dto = new MonthlyBudgetDTO.MonthlyBudgetBuilder(monthlyBudgetDTO.getUserId(), monthlyBudgetDTO.getSum()).
                id(id).date(monthlyBudgetDTO.getDate()).build();
        monthlyBudgetRepository.save(monthlyBudgetDTOMapper.mapToEntity(dto));

        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findById(id));
    }

    public boolean delete(int id) {
        MonthlyBudgetEntity monthlyBudget = monthlyBudgetRepository.findById(id);
        monthlyBudgetRepository.delete(monthlyBudget);
        monthlyBudget = monthlyBudgetRepository.findById(id);

        return monthlyBudget == null;
    }

    public MonthlyBudgetDTO findByDateAndUserId(Date date, int userId) {
        return monthlyBudgetDTOMapper.mapToDTO(monthlyBudgetRepository.findByDateAndUserId(date, userId));
    }
}
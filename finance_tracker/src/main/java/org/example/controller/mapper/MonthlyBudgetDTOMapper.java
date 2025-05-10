package org.example.controller.mapper;

import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.model.MonthlyBudgetEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonthlyBudgetDTOMapper {
    MonthlyBudgetDTO mapToDTO(MonthlyBudgetEntity monthlyBudget);

    MonthlyBudgetEntity mapToEntity(MonthlyBudgetDTO monthlyBudgetDTO);
}
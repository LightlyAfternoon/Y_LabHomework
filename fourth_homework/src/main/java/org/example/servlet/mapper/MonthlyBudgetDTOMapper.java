package org.example.servlet.mapper;

import org.example.model.MonthlyBudgetEntity;
import org.example.servlet.dto.MonthlyBudgetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MonthlyBudgetDTOMapper {
    MonthlyBudgetDTOMapper INSTANCE = Mappers.getMapper(MonthlyBudgetDTOMapper.class);

    MonthlyBudgetDTO mapToDTO(MonthlyBudgetEntity monthlyBudget);

    MonthlyBudgetEntity mapToEntity(MonthlyBudgetDTO monthlyBudgetDTO);
}
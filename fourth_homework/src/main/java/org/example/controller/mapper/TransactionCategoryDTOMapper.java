package org.example.controller.mapper;

import org.example.controller.dto.TransactionCategoryDTO;
import org.example.model.TransactionCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionCategoryDTOMapper {
    TransactionCategoryDTO mapToDTO(TransactionCategoryEntity transactionCategory);

    TransactionCategoryEntity mapToEntity(TransactionCategoryDTO transactionCategoryDTO);
}
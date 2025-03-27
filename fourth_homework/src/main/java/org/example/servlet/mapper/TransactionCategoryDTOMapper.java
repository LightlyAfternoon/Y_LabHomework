package org.example.servlet.mapper;

import org.example.model.TransactionCategoryEntity;
import org.example.servlet.dto.TransactionCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionCategoryDTOMapper {
    TransactionCategoryDTOMapper INSTANCE = Mappers.getMapper(TransactionCategoryDTOMapper.class);

    TransactionCategoryDTO mapToDTO(TransactionCategoryEntity transactionCategory);

    TransactionCategoryEntity mapToEntity(TransactionCategoryDTO transactionCategoryDTO);
}
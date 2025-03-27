package org.example.servlet.mapper;

import org.example.model.TransactionEntity;
import org.example.servlet.dto.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionDTOMapper {
    TransactionDTOMapper INSTANCE = Mappers.getMapper(TransactionDTOMapper.class);

    TransactionDTO mapToDTO(TransactionEntity transaction);

    TransactionEntity mapToEntity(TransactionDTO transactionDTO);
}
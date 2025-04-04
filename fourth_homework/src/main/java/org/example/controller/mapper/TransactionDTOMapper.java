package org.example.controller.mapper;

import org.example.controller.dto.TransactionDTO;
import org.example.model.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionDTOMapper {
    TransactionDTO mapToDTO(TransactionEntity transaction);

    TransactionEntity mapToEntity(TransactionDTO transactionDTO);
}
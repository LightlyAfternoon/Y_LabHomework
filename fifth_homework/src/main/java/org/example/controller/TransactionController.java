package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.annotation.Loggable;
import org.example.controller.dto.TransactionDTO;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Loggable
@Tag(name = "Transaction Controller")
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Get all transactions")
    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactionDTOS = transactionService.findAll();

        if (transactionDTOS != null && !transactionDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all transactions by user id")
    @GetMapping(value = {"", "/"}, params = {"user"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByUserId(@RequestParam(name = "user") int userId) {
        List<TransactionDTO> transactionDTOS = transactionService.findAllByUserId(userId);

        if (transactionDTOS != null && !transactionDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all transactions filtered by date, category id, sum type (Pos for positive sum or Neg for negative sum) and/or user id")
    @GetMapping(value = {"", "/"}, params = {"date", "category", "type", "user"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByDateAndCategoryAndTypeAndUserId(
            @RequestParam(name = "date", required = false) Date date, @RequestParam(name = "category", required = false) int categoryId,
            @RequestParam(name = "type", required = false) String type, @RequestParam(name = "user", required = false) int userId) {
        List<TransactionDTO> transactionDTOS = transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(date, categoryId, type, userId);

        if (transactionDTOS != null && !transactionDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get transaction by id")
    @GetMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable("id") int id) {
        TransactionDTO transactionDTO = transactionService.findById(id);

        if (transactionDTO != null) {
            return ResponseEntity.ok(transactionDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add new transaction")
    @PostMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        if (TransactionDTO.isValid(transactionDTO)) {
            transactionDTO = transactionService.add(transactionDTO);

            if (transactionDTO != null) {
                return ResponseEntity.ok(transactionDTO);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update transaction by id")
    @PutMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable("id") int id, @RequestBody TransactionDTO transactionDTO) {
        if (TransactionDTO.isValid(transactionDTO)) {
            transactionDTO = transactionService.update(transactionDTO, id);

            if (transactionDTO != null) {
                return ResponseEntity.ok(transactionDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete transaction by id")
    @DeleteMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> deleteTransactionById(@PathVariable("id") int id) {
        boolean isDeleted = transactionService.delete(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
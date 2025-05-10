package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.annotation.Loggable;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.service.TransactionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Loggable
@Tag(name = "Transaction Controller")
@RestController
@RequestMapping("/category")
public class TransactionCategoryController {
    TransactionCategoryService transactionCategoryService;

    @Autowired
    public TransactionCategoryController(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
    }

    @Operation(summary = "Get all transaction categories")
    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionCategoryDTO>> getAllTransactionCategories() {
        List<TransactionCategoryDTO> transactionCategoryDTOS = transactionCategoryService.findAll();

        if (transactionCategoryDTOS != null && !transactionCategoryDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionCategoryDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all transaction categories and goals by user id")
    @GetMapping(value = {"", "/"}, params = {"user"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionCategoryDTO>> getAllTransactionCategoriesAndGoalsByUserId(@RequestParam(name = "user") int userId) {
        List<TransactionCategoryDTO> transactionCategoryDTOS = transactionCategoryService.findCommonCategoriesOrGoalsByUserId(userId);

        if (transactionCategoryDTOS != null && !transactionCategoryDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionCategoryDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all goals by user id")
    @GetMapping(value = {"goal", "goal/"}, params = {"user"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionCategoryDTO>> getAllGoalsByUserId(@RequestParam(name = "user") int userId) {
        List<TransactionCategoryDTO> transactionCategoryDTOS = transactionCategoryService.findAllGoalsByUserId(userId);

        if (transactionCategoryDTOS != null && !transactionCategoryDTOS.isEmpty()) {
            return ResponseEntity.ok(transactionCategoryDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get transaction category by name")
    @GetMapping(value = {"", "/"}, params = {"user", "name"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionCategoryDTO> getTransactionCategoryByName(@RequestParam(name = "name") String name) {
        TransactionCategoryDTO transactionCategoryDTO = transactionCategoryService.findByName(name);

        if (transactionCategoryDTO != null) {
            return ResponseEntity.ok(transactionCategoryDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get transaction category by id")
    @GetMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionCategoryDTO> getTransactionCategoryById(@PathVariable("id") int id) {
        TransactionCategoryDTO transactionCategoryDTO = transactionCategoryService.findById(id);

        if (transactionCategoryDTO != null) {
            return ResponseEntity.ok(transactionCategoryDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add transaction category")
    @PostMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionCategoryDTO> createTransactionCategory(@RequestBody TransactionCategoryDTO transactionCategoryDTO) {
        if (TransactionCategoryDTO.isValid(transactionCategoryDTO)) {
            transactionCategoryDTO = transactionCategoryService.add(transactionCategoryDTO);

            if (transactionCategoryDTO != null) {
                return ResponseEntity.ok(transactionCategoryDTO);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update transaction category by id")
    @PutMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionCategoryDTO> updateTransactionCategory(@PathVariable("id") int id, @RequestBody TransactionCategoryDTO transactionCategoryDTO) {
        if (TransactionCategoryDTO.isValid(transactionCategoryDTO)) {
            transactionCategoryDTO = transactionCategoryService.update(transactionCategoryDTO, id);

            if (transactionCategoryDTO != null) {
                return ResponseEntity.ok(transactionCategoryDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete transaction category by id")
    @DeleteMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionCategoryDTO> deleteTransactionCategoryById(@PathVariable("id") int id) {
        boolean isDeleted = transactionCategoryService.delete(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
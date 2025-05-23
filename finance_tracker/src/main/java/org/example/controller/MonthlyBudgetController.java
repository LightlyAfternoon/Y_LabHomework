package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.annotation.Loggable;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.service.MonthlyBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Loggable
@Tag(name = "Monthly Budget Controller")
@RestController
@RequestMapping("/budget")
public class MonthlyBudgetController {
    MonthlyBudgetService monthlyBudgetService;

    @Autowired
    public MonthlyBudgetController(MonthlyBudgetService monthlyBudgetService) {
        this.monthlyBudgetService = monthlyBudgetService;
    }

    @Operation(summary = "Get all monthly budgets")
    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MonthlyBudgetDTO>> getAllMonthlyBudgets() {
        List<MonthlyBudgetDTO> monthlyBudgetDTOS = monthlyBudgetService.findAll();

        if (monthlyBudgetDTOS != null && !monthlyBudgetDTOS.isEmpty()) {
            return ResponseEntity.ok(monthlyBudgetDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get monthly budget by date and user id")
    @GetMapping(value = {"", "/"}, params = {"date", "user"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> getAllMonthlyBudgetsByDateAndUserId(
            @RequestParam(name = "date") Date date, @RequestParam(name = "user") int userId) {
        MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findByDateAndUserId(date, userId);

        if (monthlyBudgetDTO != null) {
            return ResponseEntity.ok(monthlyBudgetDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get monthly budget by id")
    @GetMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> getMonthlyBudgetById(@PathVariable("id") int id) {
        MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findById(id);

        if (monthlyBudgetDTO != null) {
            return ResponseEntity.ok(monthlyBudgetDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add monthly budget")
    @PostMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> createMonthlyBudget(@RequestBody MonthlyBudgetDTO monthlyBudgetDTO) {
        if (MonthlyBudgetDTO.isValid(monthlyBudgetDTO)) {
            monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

            if (monthlyBudgetDTO != null) {
                return ResponseEntity.ok(monthlyBudgetDTO);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update monthly budget by id")
    @PutMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> updateMonthlyBudget(@PathVariable("id") int id, @RequestBody MonthlyBudgetDTO monthlyBudgetDTO) {
        if (MonthlyBudgetDTO.isValid(monthlyBudgetDTO)) {
            monthlyBudgetDTO = monthlyBudgetService.update(monthlyBudgetDTO, id);

            if (monthlyBudgetDTO != null) {
                return ResponseEntity.ok(monthlyBudgetDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete monthly budget by id")
    @DeleteMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> deleteMonthlyBudgetById(@PathVariable("id") int id) {
        boolean isDeleted = monthlyBudgetService.delete(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
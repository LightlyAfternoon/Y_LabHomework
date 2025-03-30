package org.example.controller;

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
@RestController
@RequestMapping("/budget")
public class MonthlyBudgetController {
    MonthlyBudgetService monthlyBudgetService;

    @Autowired
    public MonthlyBudgetController(MonthlyBudgetService monthlyBudgetService) {
        this.monthlyBudgetService = monthlyBudgetService;
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MonthlyBudgetDTO>> getAllMonthlyBudgets() {
        List<MonthlyBudgetDTO> monthlyBudgetDTOS = monthlyBudgetService.findAll();

        if (monthlyBudgetDTOS != null && !monthlyBudgetDTOS.isEmpty()) {
            return ResponseEntity.ok(monthlyBudgetDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> getAllMonthlyBudgetsByDateAndUserId(
            @RequestAttribute(name = "date") Date date, @RequestAttribute(name = "user") int userId) {
        MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findByDateAndUserId(date, userId);

        if (monthlyBudgetDTO != null) {
            return ResponseEntity.ok(monthlyBudgetDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = {"/{id}", "/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonthlyBudgetDTO> getMonthlyBudgetById(@PathVariable("id") int id) {
        MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findById(id);

        if (monthlyBudgetDTO != null) {
            return ResponseEntity.ok(monthlyBudgetDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
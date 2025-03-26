package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.annotation.Loggable;
import org.example.repository.MonthlyBudgetRepository;
import org.example.service.MonthlyBudgetService;
import org.example.servlet.dto.MonthlyBudgetDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Loggable
@WebServlet("/budget/*")
public class MonthlyBudgetServlet extends HttpServlet {
    MonthlyBudgetService monthlyBudgetService;

    public MonthlyBudgetServlet() {
        this.monthlyBudgetService = new MonthlyBudgetService(new MonthlyBudgetRepository());
    }

    public MonthlyBudgetServlet(MonthlyBudgetService monthlyBudgetService) {
        this.monthlyBudgetService = monthlyBudgetService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM"));

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findById(id);

                if (monthlyBudgetDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(monthlyBudgetDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else if (req.getParameter("date") != null && req.getParameter("user") != null) {
            try {
                Date date = new Date(new SimpleDateFormat("yyyy-MM").parse(req.getParameter("date")).getTime());
                int userId = Integer.parseInt(req.getParameter("user"));
                MonthlyBudgetDTO monthlyBudgetDTO = monthlyBudgetService.findByDateAndUserId(date, userId);

                if (monthlyBudgetDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(monthlyBudgetDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException | ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<MonthlyBudgetDTO> monthlyBudgetDTOS = monthlyBudgetService.findAll();

                if (monthlyBudgetDTOS != null) {
                    printWriter.write(objectMapper.writeValueAsString(monthlyBudgetDTOS));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM"));
        String monthlyBudgetString = req.getReader().lines().collect(Collectors.joining());

        if (!monthlyBudgetString.isBlank()) {
            try {
                MonthlyBudgetDTO monthlyBudgetDTO = objectMapper.readValue(monthlyBudgetString, MonthlyBudgetDTO.class);
                monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

                if (monthlyBudgetDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(monthlyBudgetDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM"));
        String monthlyBudgetString = req.getReader().lines().collect(Collectors.joining());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                MonthlyBudgetDTO monthlyBudgetDTO = objectMapper.readValue(monthlyBudgetString, MonthlyBudgetDTO.class);
                monthlyBudgetDTO = monthlyBudgetService.update(monthlyBudgetDTO, id);

                if (monthlyBudgetDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(monthlyBudgetDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter printWriter = new PrintWriter(resp.getWriter());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                boolean isDeleted = monthlyBudgetService.delete(id);

                if (isDeleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    printWriter.write("MonthlyBudget deleted!");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                    printWriter.write("MonthlyBudget NOT found!");
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.TransactionService;
import org.example.service.UserService;
import org.example.servlet.dto.TransactionDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/transaction/*")
public class TransactionServlet extends HttpServlet {
    TransactionService transactionService;

    public TransactionServlet() {
        this.transactionService = new TransactionService(new TransactionRepository());
    }

    public TransactionServlet(TransactionService transactionService) {
        this.transactionService = transactionService;
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
                TransactionDTO transactionDTO = transactionService.findById(id);

                if (transactionDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<TransactionDTO> transactionDTOS = transactionService.findAll();

                if (transactionDTOS != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionDTOS));
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
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String transactionString = req.getReader().lines().collect(Collectors.joining());

        if (!transactionString.isBlank()) {
            try {
                TransactionDTO transactionDTO = objectMapper.readValue(transactionString, TransactionDTO.class);
                transactionDTO = transactionService.add(transactionDTO);

                if (transactionDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionDTO));
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
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String transactionString = req.getReader().lines().collect(Collectors.joining());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                TransactionDTO transactionDTO = objectMapper.readValue(transactionString, TransactionDTO.class);
                transactionDTO = transactionService.update(transactionDTO, id);

                if (transactionDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionDTO));
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
                boolean isDeleted = transactionService.delete(id);

                if (isDeleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    printWriter.write("Transaction deleted!");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                    printWriter.write("Transaction NOT found!");
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
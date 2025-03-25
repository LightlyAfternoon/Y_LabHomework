package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.repository.TransactionCategoryRepository;
import org.example.service.TransactionCategoryService;
import org.example.servlet.dto.TransactionCategoryDTO;
import org.example.servlet.dto.TransactionDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/category/*")
public class TransactionCategoryServlet extends HttpServlet {
    TransactionCategoryService transactionCategoryService;

    public TransactionCategoryServlet() {
        this.transactionCategoryService = new TransactionCategoryService(new TransactionCategoryRepository());
    }

    public TransactionCategoryServlet(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                TransactionCategoryDTO transactionCategoryDTO = transactionCategoryService.findById(id);

                if (transactionCategoryDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionCategoryDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else if (req.getParameter("user") != null) {
            try {
                int userId = Integer.parseInt(req.getParameter("user"));

                List<TransactionCategoryDTO> goalDTOS = transactionCategoryService.findAllGoalsWithUserId(userId);

                if (goalDTOS != null) {
                    printWriter.write(objectMapper.writeValueAsString(goalDTOS));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else if (req.getParameter("name") != null) {
            String name = req.getParameter("name");

            try {
                TransactionCategoryDTO transactionCategoryDTO = transactionCategoryService.findByName(name);

                if (transactionCategoryDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionCategoryDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<TransactionCategoryDTO> transactionCategoryDTOS = transactionCategoryService.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId());

                if (transactionCategoryDTOS != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionCategoryDTOS));
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
        String transactionCategoryString = req.getReader().lines().collect(Collectors.joining());

        if (!transactionCategoryString.isBlank()) {
            try {
                TransactionCategoryDTO transactionCategoryDTO = objectMapper.readValue(transactionCategoryString, TransactionCategoryDTO.class);
                transactionCategoryDTO = transactionCategoryService.add(transactionCategoryDTO);

                if (transactionCategoryDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionCategoryDTO));
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
        String transactionCategoryString = req.getReader().lines().collect(Collectors.joining());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                TransactionCategoryDTO transactionCategoryDTO = objectMapper.readValue(transactionCategoryString, TransactionCategoryDTO.class);
                transactionCategoryDTO = transactionCategoryService.update(transactionCategoryDTO, id);

                if (transactionCategoryDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(transactionCategoryDTO));
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
                boolean isDeleted = transactionCategoryService.delete(id);

                if (isDeleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    printWriter.write("TransactionCategory deleted!");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                    printWriter.write("TransactionCategory NOT found!");
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
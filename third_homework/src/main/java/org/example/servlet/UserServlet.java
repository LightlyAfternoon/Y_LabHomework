package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.service.UserService;
import org.example.servlet.dto.UserDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    UserService userService;

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                UserDTO userDTO = userService.findById(id);

                if (userDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(userDTO));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<UserDTO> userDTOS = userService.findAll();

                if (userDTOS != null) {
                    printWriter.write(objectMapper.writeValueAsString(userDTOS));
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
        String userString = req.getReader().lines().collect(Collectors.joining());

        if (!userString.isBlank()) {
            try {
                UserDTO userDTO = objectMapper.readValue(userString, UserDTO.class);
                userDTO = userService.add(userDTO);

                if (userDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(userDTO));
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
        String userString = req.getReader().lines().collect(Collectors.joining());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                UserDTO userDTO = objectMapper.readValue(userString, UserDTO.class);
                userDTO = userService.update(userDTO, id);

                if (userDTO != null) {
                    printWriter.write(objectMapper.writeValueAsString(userDTO));
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
        ObjectMapper objectMapper = new ObjectMapper();
        String userString = req.getReader().lines().collect(Collectors.joining());

        if (req.getPathInfo() != null && req.getPathInfo().split("/").length > 1) {
            int id = Integer.parseInt(req.getPathInfo().split("/")[1]);

            try {
                boolean isDeleted = userService.delete(id);

                if (isDeleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    printWriter.write("User deleted!");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                    printWriter.write("User NOT found!");
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
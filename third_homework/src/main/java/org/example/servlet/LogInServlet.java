package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.servlet.dto.LogInDTO;
import org.example.servlet.dto.UserDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.stream.Collectors;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {
    UserService userService;

    public LogInServlet() {
        this.userService = new UserService(new UserRepository());
    }

    public LogInServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter printWriter = new PrintWriter(resp.getWriter());
        ObjectMapper objectMapper = new ObjectMapper();
        String userString = req.getReader().lines().collect(Collectors.joining());

        if (!userString.isBlank()) {
            try {
                LogInDTO logInDTO = objectMapper.readValue(userString, LogInDTO.class);
                UserDTO userDTO = userService.findUserWithEmailAndPassword(logInDTO.getEmail(), logInDTO.getPassword());

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
}
package org.example.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.dto.LogInDTO;
import org.example.servlet.dto.UserDTO;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HttpRequestsClass {
    Scanner scanner;

    UserDTO getLoggedInUser() throws JsonProcessingException {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/second_homework-1.0-SNAPSHOT/login");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                LogInDTO logInDTO = new LogInDTO.LogInBuilder(sendEmail(), sendPassword()).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(logInDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            UserDTO userDTO = null;

            if (httpURLConnection.getResponseCode() != 404) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                userDTO = objectMapper.readValue(stringBuilder.toString(), UserDTO.class);
            }

            httpURLConnection.disconnect();

            return userDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<UserDTO> getAllUsers() throws JsonProcessingException {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/second_homework-1.0-SNAPSHOT/user");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.connect();

            List<UserDTO> userDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                userDTOS = Arrays.asList(objectMapper.readValue(stringBuilder.toString(), UserDTO[].class));
            }

            httpURLConnection.disconnect();

            return userDTOS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    UserDTO getRegisteredUser() throws JsonProcessingException {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/second_homework-1.0-SNAPSHOT/user");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                UserDTO userDTO = new UserDTO.UserBuilder(sendEmail(), sendPassword(), sendUserName()).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(userDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            UserDTO userDTO = null;

            if (httpURLConnection.getResponseCode() != 404) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                userDTO = objectMapper.readValue(stringBuilder.toString(), UserDTO.class);
            }

            httpURLConnection.disconnect();

            return userDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sendEmail() {
        scanner = new Scanner(System.in);

        System.out.println("Введите почту:");

        return scanner.next();
    }

    private String sendPassword() {
        System.out.println("Введите пароль:");

        return scanner.next();
    }

    private String sendUserName() {
        System.out.println("Введите имя:");

        return scanner.next();
    }
}
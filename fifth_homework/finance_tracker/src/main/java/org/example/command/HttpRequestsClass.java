package org.example.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.CurrentUser;
import org.example.controller.dto.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains methods for sending HTTP requests.
 * DataOutputStreams represent a request body.
 * BufferedReaders represent a response body.
 */
public class HttpRequestsClass {
    public UserDTO getLoggedInUser(String email, String password) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/login");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                LogInDTO logInDTO = new LogInDTO.LogInBuilder(email, password).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(logInDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            UserDTO userDTO = null;

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
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

    public List<UserDTO> getAllUsers() {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/user");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            List<UserDTO> userDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
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

    public UserDTO getRegisteredUser(String email, String password, String name) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/user");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                UserDTO userDTO = new UserDTO.UserBuilder(email, password, name).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(userDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            UserDTO userDTO = null;

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
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

    public MonthlyBudgetDTO addBudget(BigDecimal budget) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String path = "http://localhost:8080/budget";
            MonthlyBudgetDTO budgetDTO = getBudget();
            if (budgetDTO != null) {
                path += "/" + budgetDTO.getId();
            }
            URL url = new URL(path);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            if (budgetDTO == null) {
                httpURLConnection.setRequestMethod("POST");
            } else {
                httpURLConnection.setRequestMethod("PUT");
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                budgetDTO = new MonthlyBudgetDTO.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), budget).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(budgetDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                budgetDTO = objectMapper.readValue(stringBuilder.toString(), MonthlyBudgetDTO.class);
            }

            httpURLConnection.disconnect();

            return budgetDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetDTO getBudget() {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            MonthlyBudgetDTO budgetDTO = new MonthlyBudgetDTO.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(0)).build();
            SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy-MM");
            Date date = new Date(yearAndMonthDateFormat.parse(budgetDTO.getDate().toString()).getTime());
            URL url = new URL("http://localhost:8080/budget?date="+date+"&user="+CurrentUser.currentUser.getId());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            budgetDTO = null;

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                budgetDTO = objectMapper.readValue(stringBuilder.toString(), MonthlyBudgetDTO.class);
            }

            httpURLConnection.disconnect();

            return budgetDTO;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionCategoryDTO addGoal(String name, BigDecimal sum) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/category");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                TransactionCategoryDTO goalDTO = new TransactionCategoryDTO.TransactionCategoryBuilder(name).
                        neededSum(sum).userId(CurrentUser.currentUser.getId()).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(goalDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            TransactionCategoryDTO goalDTO = null;

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                goalDTO = objectMapper.readValue(stringBuilder.toString(), TransactionCategoryDTO.class);
            }

            httpURLConnection.disconnect();

            return goalDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionDTO addTransaction(BigDecimal sum, int categoryId, Date date, String description) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/transaction");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                TransactionDTO transactionDTO = new TransactionDTO.TransactionBuilder(sum, CurrentUser.currentUser.getId()).
                        date(date).categoryId(categoryId).description(description).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(transactionDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();

            TransactionDTO transactionDTO = new TransactionDTO();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                transactionDTO = objectMapper.readValue(stringBuilder.toString(), TransactionDTO.class);
            }

            httpURLConnection.disconnect();

            return transactionDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteAccount(int id) {
        HttpURLConnection httpURLConnection;

        try {
            URL url = new URL("http://localhost:8080/user/"+id);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("DELETE");

            httpURLConnection.connect();
            httpURLConnection.disconnect();

            return httpURLConnection.getResponseCode() == HttpServletResponse.SC_NO_CONTENT;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TransactionDTO> getTransactions() {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/transaction?user="+CurrentUser.currentUser.getId());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            List<TransactionDTO> transactionDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                transactionDTOS = Arrays.asList(objectMapper.readValue(stringBuilder.toString(), TransactionDTO[].class));
            }

            httpURLConnection.disconnect();

            return transactionDTOS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TransactionDTO> filterTransactions(Date date, int categoryId, String type, int userId) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/transaction?date="+date+"&category="+categoryId+"&type="+type+"&user="+userId);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            List<TransactionDTO> transactionDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                transactionDTOS = Arrays.asList(objectMapper.readValue(stringBuilder.toString(), TransactionDTO[].class));
            }

            httpURLConnection.disconnect();

            return transactionDTOS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean editTransaction(int id, BigDecimal sum, int categoryId, Date date, String description) {
        HttpURLConnection httpURLConnection;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/transaction/"+id);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                TransactionDTO transactionDTO = new TransactionDTO.TransactionBuilder(sum, CurrentUser.currentUser.getId()).
                id(id).date(date).categoryId(categoryId).description(description).build();

                dataOutputStream.writeBytes(objectMapper.writeValueAsString(transactionDTO));
                dataOutputStream.flush();
            }

            httpURLConnection.connect();
            httpURLConnection.disconnect();

            return httpURLConnection.getResponseCode() != HttpServletResponse.SC_NOT_FOUND;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteTransaction(int id) {
        HttpURLConnection httpURLConnection;

        try {
            URL url = new URL("http://localhost:8080/transaction/"+id);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();
            httpURLConnection.disconnect();

            return httpURLConnection.getResponseCode() == HttpServletResponse.SC_NO_CONTENT;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TransactionCategoryDTO> getAllUserGoals(int userId) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/category/goal?user="+userId);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            List<TransactionCategoryDTO> categoryDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                categoryDTOS = Arrays.asList(objectMapper.readValue(stringBuilder.toString(), TransactionCategoryDTO[].class));
            }

            httpURLConnection.disconnect();

            return categoryDTOS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TransactionCategoryDTO> getAllCommonCategoriesOrGoalsWithCurrentUser() {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/category?user="+CurrentUser.currentUser.getId());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            List<TransactionCategoryDTO> categoryDTOS = new ArrayList<>();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                categoryDTOS = Arrays.asList(objectMapper.readValue(stringBuilder.toString(), TransactionCategoryDTO[].class));
            }

            httpURLConnection.disconnect();

            return categoryDTOS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionCategoryDTO getCategoryOrGoalWithName(String name) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/category?user=&name="+name);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                categoryDTO = objectMapper.readValue(stringBuilder.toString(), TransactionCategoryDTO.class);
            }

            httpURLConnection.disconnect();

            return categoryDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionCategoryDTO getCategoryOrGoalWithId(int id) {
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            URL url = new URL("http://localhost:8080/category/"+id);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();

            TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

            if (httpURLConnection.getResponseCode() != 404 && httpURLConnection.getResponseCode() != 400) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                categoryDTO = objectMapper.readValue(stringBuilder.toString(), TransactionCategoryDTO.class);
            }

            httpURLConnection.disconnect();

            return categoryDTO;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
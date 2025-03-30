package org.example.service;

import org.example.controller.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO add(UserDTO userDTO);

    UserDTO findById(int id);

    List<UserDTO> findAll();

    UserDTO update(UserDTO userDTO, int id);

    boolean delete(int id);

    UserDTO findUserWithEmailAndPassword(String email, String password);
}
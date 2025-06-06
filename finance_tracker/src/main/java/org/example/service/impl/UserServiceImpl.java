package org.example.service.impl;

import org.example.annotation.Loggable;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.controller.dto.UserDTO;
import org.example.controller.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Loggable
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserDTOMapper userDTOMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDTOMapper userDTOMapper) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
    }

    public UserDTO add(UserDTO userDTO) {
        UserDTO dto = new UserDTO.UserBuilder(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName()).
                role(userDTO.getRole()).isBlocked(userDTO.isBlocked()).build();

        if (userRepository.findByEmail(userDTO.getEmail()) == null) {
            return userDTOMapper.mapToDTO(userRepository.save(userDTOMapper.mapToEntity(dto)));
        } else {
            return null;
        }
    }

    public UserDTO findById(int id) {
        return userDTOMapper.mapToDTO(userRepository.findById(id));
    }

    public List<UserDTO> findAll() {

        return userRepository.findAll().stream().map(userDTOMapper::mapToDTO).toList();
    }

    public UserDTO update(UserDTO userDTO, int id) {
        UserDTO dto = new UserDTO.UserBuilder(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName()).
                id(id).role(userDTO.getRole()).isBlocked(userDTO.isBlocked()).build();

        UserEntity userWithEmail = userRepository.findByEmail(userDTO.getEmail());

        if (userWithEmail == null || userWithEmail.getId() == id) {
            return userDTOMapper.mapToDTO(userRepository.save(userDTOMapper.mapToEntity(dto)));
        } else {
            return null;
        }
    }

    public boolean delete(int id) {
        UserEntity user = userRepository.findById(id);
        userRepository.delete(user);
        user = userRepository.findById(id);

        return user == null;
    }

    public UserDTO findUserByEmailAndPassword(String email, String password) {
        return userDTOMapper.mapToDTO(userRepository.findByEmailAndPassword(email, password));
    }
}
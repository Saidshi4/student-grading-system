package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.UserMapper;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.service.firebase.FirebaseService;
import com.supremecourt.studentgradingsystem.service.firebase.MediaService;
import com.supremecourt.studentgradingsystem.utils.UsernameAndPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final FirebaseService firebaseService;
    private final UsernameAndPasswordGenerator usernameAndPasswordGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserEntity findById(Long userId) {
        log.info("ActionLog.userFindById.start userId {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.USER_NOT_FOUND.name(), String.format(ExceptionEnum.USER_NOT_FOUND.getLog(), userId)
                ));
        log.info("ActionLog.userFindById.end userId {}", userId);
        return userEntity;
    }

    public void createUser(UserRegistrationDto userRegistrationDto) {
        log.info("ActionLog.createUser.start username {}", userRegistrationDto.getFirstName());
        UserEntity userEntity = userMapper.mapUserRegistrationDtoToEntity(userRegistrationDto);
        userEntity.setUsername(usernameAndPasswordGenerator.generateUsername(userRegistrationDto));
        String password = usernameAndPasswordGenerator.generatePassword(userRegistrationDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        String role = userRegistrationDto.getRole().toUpperCase();
        RoleEntity roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.ROLE_NOT_FOUND.name(), String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), role)
                ));
        userEntity.setRole(roleEntity);
        userRepository.save(userEntity);
        log.info("ActionLog.createUser.end username {}", userRegistrationDto.getFirstName());
    }

    public UserGetDto changeImage(Long userId, MultipartFile image) {
        log.info("ActionLog.changeImage.start userId {}", userId);

        UserEntity userEntity = findById(userId);

        String imageUrl = mediaService.uploadToFirebase(image, "IMAGE");

        userEntity.setImageUrl(imageUrl);
        UserEntity updatedUser = userRepository.save(userEntity);

        log.info("ActionLog.changeImage.end userId {}", userId);
        return userMapper.mapEntityToGetDto(updatedUser);
    }

    @Transactional
    public UserGetDto deleteImage(Long userId) {
        log.info("ActionLog.deleteImage.start userId {}", userId);
        UserEntity userEntity = findById(userId);
        if (userEntity.getImageUrl() == null) {
            log.warn("ActionLog.deleteImage.warn: User has no image to delete. userId={}", userId);
            return userMapper.mapEntityToGetDto(userEntity);
        }
        String imageUrl = userEntity.getImageUrl();
        try {
            firebaseService.deleteMedia(imageUrl);
        } catch (Exception e) {
            log.error("ActionLog.deleteImage.error: Firebase delete failed for userId {}", userId, e);
        }
        userEntity.setImageUrl(null);
        UserEntity savedUser = userRepository.save(userEntity);
        UserGetDto savedDto = userMapper.mapEntityToGetDto(savedUser);
        log.info("ActionLog.deleteImage.end userId {}", userId);
        return savedDto;
    }


}

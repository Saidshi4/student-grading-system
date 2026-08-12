package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.UserMapper;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.service.firebase.FirebaseService;
import com.supremecourt.studentgradingsystem.service.firebase.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public UserEntity findById(Long userId) {
        log.info("ActionLog.userFindById.start userId {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.USER_NOT_FOUND.name(), String.format(ExceptionEnum.USER_NOT_FOUND.getLog(), userId)
                ));
        log.info("ActionLog.userFindById.end userId {}", userId);
        return userEntity;
    }

    public List<UserGetDto> filterUsersByFullName(String fullName) {
        log.info("ActionLog.filterUsersByFullName.start fullName {}", fullName);
        List<UserEntity> userEntities = userRepository.findByFullNameContainingIgnoreCase(fullName).stream()
                .filter(UserEntity::isEnabled)
                .toList();
        List<UserGetDto> userGetDtos = userMapper.mapEntityListToGetDtoList(userEntities);
        log.info("ActionLog.filterUsersByFullName.end fullName {}", fullName);
        return userGetDtos;
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

    @Transactional
    public UserEntity findOrCreateWalkInUser(String phoneNumber, String fullName) {
        log.info("ActionLog.findOrCreateWalkInUser.start phoneNumber {} fullName {}", phoneNumber, fullName);

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            UserEntity walkIn = userRepository.findByPhoneNumber("+994000000000")
                    .orElseGet(() -> {
                        UserEntity u = new UserEntity();
                        u.setFullName("Walk-in Customer");
                        u.setPhoneNumber("+994000000000");
                        u.setTokenVersion(0);
                        roleRepository.findByName("USER").ifPresent(u::setRole);
                        return userRepository.save(u);
                    });
            log.info("ActionLog.findOrCreateWalkInUser.end default walk-in user resolved: id={}", walkIn.getId());
            return walkIn;
        }

        String cleanedPhone = phoneNumber.trim();
        UserEntity user = userRepository.findByPhoneNumber(cleanedPhone)
                .orElseGet(() -> {
                    UserEntity u = new UserEntity();
                    u.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : "Guest Customer");
                    u.setPhoneNumber(cleanedPhone);
                    u.setTokenVersion(0);
                    roleRepository.findByName("USER").ifPresent(u::setRole);
                    return userRepository.save(u);
                });
        log.info("ActionLog.findOrCreateWalkInUser.end resolved user: id={}", user.getId());
        return user;
    }
}

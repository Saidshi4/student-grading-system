package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.StudentEntity;
import com.supremecourt.studentgradingsystem.dao.entity.TeacherEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.GroupRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyExistsException;
import com.supremecourt.studentgradingsystem.mapper.UserMapper;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.request.UserUpdateDto;
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
    private final GroupRepository groupRepository;
    private final FirebaseService firebaseService;
    private final UsernameAndPasswordGenerator usernameAndPasswordGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserEntity findById(Long userId) {
        log.info("ActionLog.userFindById.start userId {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.USER_NOT_FOUND.getMessage(), String.format(ExceptionEnum.USER_NOT_FOUND.getLog(), userId)
                ));
        log.info("ActionLog.userFindById.end userId {}", userId);
        return userEntity;
    }

    public UserGetDto getById(Long userId) {
        return userMapper.mapEntityToGetDto(findById(userId));
    }

    public List<UserGetDto> getAll() {
        log.info("ActionLog.getAllUsers.start");
        List<UserGetDto> result = userMapper.mapEntityListToGetDtoList(userRepository.findAll());
        log.info("ActionLog.getAllUsers.end");
        return result;
    }

    @Transactional
    public UserEntity createUser(UserRegistrationDto userRegistrationDto) {
        log.info("ActionLog.createUser.start username {}", userRegistrationDto.getFirstName());
        if (userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.USER_ALREADY_EXISTS.getMessage(),
                    String.format(ExceptionEnum.USER_ALREADY_EXISTS.getLog(), userRegistrationDto.getEmail())
            );
        }
        String role = userRegistrationDto.getRole().toUpperCase();
        RoleEntity roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.ROLE_NOT_FOUND.getMessage(), String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), role)
                ));

        UserEntity userEntity = switch (role) {
            case "STUDENT" -> new StudentEntity();
            case "TEACHER" -> new TeacherEntity();
            default -> new UserEntity();
        };
        userMapper.mapUserRegistrationDtoToEntity(userRegistrationDto, userEntity);
        userEntity.setUsername(usernameAndPasswordGenerator.generateUsername(userRegistrationDto));
        userEntity.setPassword(passwordEncoder.encode(
                usernameAndPasswordGenerator.generatePassword(userRegistrationDto)));
        userEntity.setRole(roleEntity);

        if (userEntity instanceof StudentEntity student && userRegistrationDto.getGroupId() != null) {
            student.setGroup(groupRepository.findById(userRegistrationDto.getGroupId())
                    .orElseThrow(() -> new NotFoundException(
                            ExceptionEnum.GROUP_NOT_FOUND.getMessage(),
                            String.format(ExceptionEnum.GROUP_NOT_FOUND.getLog(), userRegistrationDto.getGroupId())
                    )));
        }
        if (userEntity instanceof TeacherEntity teacher) {
            teacher.setDepartment(userRegistrationDto.getDepartment());
        }

        UserEntity saved = userRepository.save(userEntity);
        log.info("ActionLog.createUser.end username {}", userRegistrationDto.getFirstName());
        return saved;
    }

    @Transactional
    public UserGetDto update(Long id, UserUpdateDto dto) {
        log.info("ActionLog.updateUser.start userId {}", id);
        UserEntity userEntity = findById(id);
        userMapper.updateEntityFromDto(dto, userEntity);
        UserGetDto result = userMapper.mapEntityToGetDto(userRepository.save(userEntity));
        log.info("ActionLog.updateUser.end userId {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteUser.start userId {}", id);
        UserEntity userEntity = findById(id);
        if (userEntity instanceof TeacherEntity teacher
                && teacher.getCourseOfferings() != null
                && !teacher.getCourseOfferings().isEmpty()) {
            throw new IsNotEmptyException("Teacher has course offerings and cannot be deleted",
                    "ActionLog.deleteUser.error teacher " + id + " has course offerings");
        }
        userRepository.delete(userEntity);
        log.info("ActionLog.deleteUser.end userId {}", id);
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

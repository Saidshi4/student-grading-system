package com.supremecourt.studentgradingsystem.service.permission;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.RoleMapper;
import com.supremecourt.studentgradingsystem.model.matrix.RoleGetDto;
import com.supremecourt.studentgradingsystem.model.request.RoleSaveDto;
import com.supremecourt.studentgradingsystem.model.request.RoleUpdateDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    public void createRole(RoleSaveDto roleSaveDto){
        log.info("ActionLog.createRole.start");
        roleSaveDto.setName(roleSaveDto.getName().trim().toUpperCase().replace(' ','_'));
        roleRepository.save(roleMapper.mapSaveDtoToEntity(roleSaveDto));
        log.info("ActionLog.createRole.end");
    }
    public List<RoleGetDto> getAllRoles(){
        log.info("ActionLog.getAllRoles.start");
        var roleGetDto= roleMapper.mapEntityToGetDtos(roleRepository.findAll());
        log.info("ActionLog.getAllRoles.end");
        return roleGetDto;
    }
    public void updateRole(RoleUpdateDto roleUpdateDto){
        log.info("ActionLog.updateRole.start");
        roleUpdateDto.setName(roleUpdateDto.getName().toUpperCase().replace(' ','_'));
        roleUpdateDto.setUpdatedAt(Instant.now());
        roleRepository.save(roleMapper.mapUpdateDtoToEntity(roleUpdateDto));
        log.info("ActionLog.updateRole.end");
    }

    @Transactional
    public void deleteRole(Long roleId) {
        log.info("ActionLog.deleteRole.start by roleId {}", roleId);

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.ROLE_NOT_FOUND.name(),
                        String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), roleId)
                ));

        // Silinən rolu USER-ə dəyişmək üçün default rolu tapırıq
        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.ROLE_NOT_FOUND.name(),
                        String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), "USER")
                ));

        // Əgər USER rolunu silmək istəyirsə, icazə vermə (istəmirsənsə bu hissəni sil)
        if ("USER".equals(role.getName())) {
            throw new IllegalArgumentException("USER rolu silinə bilməz.");
        }

        // User list null ola bilər
        if (role.getUserEntities() != null && !role.getUserEntities().isEmpty()) {
            for (UserEntity user : role.getUserEntities()) {
                user.setRole(userRole);
            }
            userRepository.saveAll(role.getUserEntities());
        }

        roleRepository.delete(role);

        log.info("ActionLog.deleteRole.end by roleId {}", roleId);
    }
}

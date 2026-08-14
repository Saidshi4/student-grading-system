package com.supremecourt.studentgradingsystem.controller.permission;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.matrix.RoleGetDto;
import com.supremecourt.studentgradingsystem.model.request.RoleSaveDto;
import com.supremecourt.studentgradingsystem.model.request.RoleUpdateDto;
import com.supremecourt.studentgradingsystem.service.permission.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @PostMapping()
    @RequiresPermission("role_create")
    public ResponseEntity<?> createRole(@RequestBody RoleSaveDto roleSaveDto){
        roleService.createRole(roleSaveDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role Successfully Created");
    }
    @GetMapping()
    @RequiresPermission("role_read")
    public List<RoleGetDto> getAllRoles(){
        return roleService.getAllRoles();
    }
    @PutMapping()
    @RequiresPermission("role_update")
    public ResponseEntity<?> updateRole(@RequestBody RoleUpdateDto roleUpdateDto){
        roleService.updateRole(roleUpdateDto);
        return ResponseEntity.ok("Role Successfully Updated");
    }
    @DeleteMapping("/{roleId}")
    @RequiresPermission("role_delete")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId){
        roleService.deleteRole(roleId);
        return ResponseEntity.ok("Role Successfully Deleted");
    }
}

package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.GroupSaveDto;
import com.supremecourt.studentgradingsystem.model.response.GroupGetDto;
import com.supremecourt.studentgradingsystem.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("group_create")
    public GroupGetDto create(@Valid @RequestBody GroupSaveDto dto) {
        return groupService.create(dto);
    }

    @GetMapping
    public List<GroupGetDto> getAll() {
        return groupService.getAll();
    }

    @GetMapping("/{id}")
    public GroupGetDto getById(@PathVariable Long id) {
        return groupService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("group_update")
    public GroupGetDto update(@PathVariable Long id, @Valid @RequestBody GroupSaveDto dto) {
        return groupService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("group_delete")
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }
}

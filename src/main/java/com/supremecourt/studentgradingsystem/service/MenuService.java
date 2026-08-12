package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.ComponentEntity;
import com.supremecourt.studentgradingsystem.dao.entity.MenuEntity;
import com.supremecourt.studentgradingsystem.dao.repository.MenuRepository;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.ComponentMapper;
import com.supremecourt.studentgradingsystem.mapper.MenuMapper;
import com.supremecourt.studentgradingsystem.model.request.MenuSaveDto;
import com.supremecourt.studentgradingsystem.model.response.ComponentResponseDto;
import com.supremecourt.studentgradingsystem.model.response.MenuResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final ComponentMapper componentMapper;
    public List<MenuResponseDto> getMenusForClaims(List<Long> claimIds) {
        log.info("ActionLog.getMenusForClaims.start");

        List<MenuEntity> menus = menuRepository.findMenusByClaimIds(claimIds);


        List<MenuResponseDto> menuResponseDtos = new ArrayList<>();

        for (MenuEntity menu : menus) {
            MenuResponseDto menuDto = new MenuResponseDto();
            menuDto.setId(menu.getId());
            menuDto.setName(menu.getName());
            menuDto.setIcon(menu.getIcon());
            menuDto.setPath(menu.getPath());
            menuDto.setIsVisible(menu.getIsVisible());
            menuDto.setCreatedAt(menu.getCreatedAt());
            menuDto.setUpdatedAt(menu.getUpdatedAt());

            List<ComponentEntity> filteredComponents = menu.getComponents().stream()
                    .filter(component -> claimIds.contains(component.getClaims().getId()))
                    .collect(Collectors.toList());
            List<ComponentResponseDto> componentDtos = componentMapper.mapComponentEntityToResponseDtos(filteredComponents);
            menuDto.setComponentResponseDtos(componentDtos);

            menuResponseDtos.add(menuDto);
        }
        log.info("ActionLog.getMenusForClaims.end");
        return menuResponseDtos;
    }

//    public List<MenuResponseDto> getMenusForClaims(List<Long> claimIds) {
//        log.info("ActionLog.getMenusForClaims.start");
//        List<MenuEntity> menus = menuRepository.findByClaimIdIn(claimIds);
//        List<MenuResponseDto> menuResponseDtos = menuMapper.mapMenuEntityToResponseDtos(menus);
//        List<MenuResponseDto> menuDtos = new ArrayList<>();
//        for (MenuResponseDto menu : menuResponseDtos) {
//            MenuResponseDto menuDto = new MenuResponseDto();
//            menuDto.setId(menu.getId());
//            menuDto.setName(menu.getName());
//            menuDto.setIcon(menu.getIcon());
//            menuDto.setPath(menu.getPath());
//            menuDto.setCreatedAt(menu.getCreatedAt());
//            menuDto.setUpdatedAt(menu.getUpdatedAt());
//
//            List<SubMenuEntity> subMenus = subMenuRepository.findByClaimIdInAndMenuId(claimIds, menu.getId());
//            List<SubMenuResponseDto> subMenuDtos = subMenuMapper.mapSubMenuEntityToResponseDtos(subMenus);
//            menuDto.setSubMenuEntities(subMenuDtos);
//            menuDtos.add(menuDto);
//        }
//        log.info("ActionLog.getMenusForClaims.end");
//        return menuDtos;
//    }
    public void createMenu(MenuSaveDto menuSaveDto){
        log.info("ActionLog.createMenu.start");
        MenuEntity menuEntity=menuMapper.mapSaveDtoToEntity(menuSaveDto);
        menuRepository.save(menuEntity);
        log.info("ActionLog.createMenu.end");
    }

    public MenuEntity getMenu(String name){
        log.info("ActionLog.updateMenu.start by menuName {}",name);
        var menuEntity=menuRepository.findByName(name).orElseThrow(()->{
            throw new NotFoundException("Menu tapılmadı!",
                    String.format("ActionLog.updateMenu.error menu by id %s not found",name));
        });
        log.info("ActionLog.updateMenu.end by menuName {}",name);
        return menuEntity;
    }
}

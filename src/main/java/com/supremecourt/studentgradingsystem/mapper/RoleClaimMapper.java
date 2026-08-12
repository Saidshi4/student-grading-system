package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.RolesClaimsEntity;
import com.supremecourt.studentgradingsystem.model.matrix.RoleClaimGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleClaimMapper {
    @Mapping(target="roleId",source = "role.id")
    @Mapping(target="claimId",source = "claim.id")
    RoleClaimGetDto mapEntityToGetDto(RolesClaimsEntity rolesClaimsEntity);
    List<RoleClaimGetDto> mapEntityToGetDtos(List<RolesClaimsEntity> rolesClaimsEntities);

}

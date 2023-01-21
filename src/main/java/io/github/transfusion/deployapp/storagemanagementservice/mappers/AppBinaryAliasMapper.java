package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.AppBinaryAliasDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAlias;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppBinaryAliasMapper {
    @Mapping(source = "id", target = "alias")
    AppBinaryAliasDTO toDTO(AppBinaryAlias appBinaryAlias);
}

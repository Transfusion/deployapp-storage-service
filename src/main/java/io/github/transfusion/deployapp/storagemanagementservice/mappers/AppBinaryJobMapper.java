package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.AppBinaryJobDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryJob;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface AppBinaryJobMapper {
    AppBinaryJobDTO mapAppBinaryJobToDTO(AppBinaryJob job);
}

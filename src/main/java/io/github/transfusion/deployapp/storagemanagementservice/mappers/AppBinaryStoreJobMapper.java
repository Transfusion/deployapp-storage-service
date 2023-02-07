package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.AppBinaryStoreJobDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppBinaryStoreJobMapper {
    @Mapping(target = "name", source = "job.appBinary.name")
    @Mapping(target = "appBinaryId", source = "job.appBinary.id")
    AppBinaryStoreJobDTO toDTO(AppBinaryStoreJob job);
}

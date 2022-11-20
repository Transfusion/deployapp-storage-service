package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.AppBinaryDownloadDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryDownload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppBinaryDownloadMapper {
    AppBinaryDownloadDTO toDTO(AppBinaryDownload appBinaryDownload);
}

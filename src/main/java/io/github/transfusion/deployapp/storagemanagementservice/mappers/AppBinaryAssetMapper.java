package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.AppBinaryAssetDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppBinaryAssetMapper {

    @Mapping(target = "appBinaryId", expression = "java( asset.getAppBinary().getId().toString() )")
    AppBinaryAssetDTO mapAppBinaryAssetToDTO(AppBinaryAsset asset);
}

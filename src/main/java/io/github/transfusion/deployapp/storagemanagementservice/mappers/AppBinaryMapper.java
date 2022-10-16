package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.ApkDTO;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.dto.response.IpaDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppBinaryMapper {

    default AppBinaryDTO toDTO(AppBinary a) {
        if (a instanceof Ipa) {
            return mapIpaToDTO((Ipa) a);
        } else if (a instanceof Apk) {
            return mapApkToDTO((Apk) a);
        } else {
            throw new IllegalArgumentException(String.format("unimplemented AppBinary subclass encountered during mapping: %s", a.getClass().getName()));
        }
    }

    AppBinaryMapper instance = Mappers.getMapper(AppBinaryMapper.class);

    //    @Mapping(target = "type", constant = S3Credential.IDENTIFIER)
    IpaDTO mapIpaToDTO(Ipa s);

    ApkDTO mapApkToDTO(Apk s);

}
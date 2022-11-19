package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.deployapp.dto.response.ApkCertDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.ApkCert;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApkCertMapper {
    ApkCertDTO toDTO(ApkCert cert);
}

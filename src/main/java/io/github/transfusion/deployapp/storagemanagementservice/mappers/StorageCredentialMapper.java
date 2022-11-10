package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.transfusion.deployapp.dto.response.FtpCredentialDTO;
import io.github.transfusion.deployapp.dto.response.S3CredentialDTO;
import io.github.transfusion.deployapp.dto.response.StorageCredentialDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface StorageCredentialMapper {

    default StorageCredentialDTO fromJsonNode(ObjectMapper objectMapper, JsonNode dto) throws JsonProcessingException {
        switch (dto.get("type").textValue()) {
            case S3Credential
                    .IDENTIFIER:
                return objectMapper.treeToValue(dto, S3CredentialDTO.class);
            case FtpCredential.IDENTIFIER:
                return objectMapper.treeToValue(dto, FtpCredentialDTO.class);
            default:
                throw new IllegalArgumentException(String.format("Unrecognized credential type %s", dto.get("type").textValue()));
        }
    }

    default StorageCredential mapStorageCredentialFromDTO(StorageCredentialDTO dto) {
        if (dto instanceof S3CredentialDTO) {
            S3Credential g = mapS3CredentialFromDTO((S3CredentialDTO) dto);
            return g;
        } else if (dto instanceof FtpCredentialDTO) {
            FtpCredential f = mapFtpCredentialFromDTO((FtpCredentialDTO) dto);
            return f;
        }
        return null;
    }

    //    @Mapping(target = "type", constant = S3Credential.IDENTIFIER)
    S3Credential mapS3CredentialFromDTO(S3CredentialDTO dto);

    FtpCredential mapFtpCredentialFromDTO(FtpCredentialDTO dto);
}

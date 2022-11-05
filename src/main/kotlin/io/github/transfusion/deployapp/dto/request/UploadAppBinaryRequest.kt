package io.github.transfusion.deployapp.dto.request

import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*

data class UploadAppBinaryRequest(
    val credentialCreatedOn: Instant,
    val binary: MultipartFile,
    val storageCredentialId: UUID,
    val organizationId: UUID?,
)
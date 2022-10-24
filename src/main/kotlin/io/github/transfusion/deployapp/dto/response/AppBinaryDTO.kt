package io.github.transfusion.deployapp.dto.response

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface AppBinaryDTO {
    val id: UUID
    val type: String
    val version: String
    val build: String
    val uploadDate: Instant
    val name: String
    val lastInstallDate: Instant?
    val identifier: String
    val assetsOnFrontPage: Boolean
    val sizeBytes: BigDecimal
    val fileName: String
    val storageCredential: UUID
    val description: String?
}

package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class IpaDTO(
    override val id: UUID,
    override val version: String,
    override val build: String,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    override val uploadDate: Instant,
    override val name: String,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    override val lastInstallDate: Instant?,
    override val identifier: String,
    override val assetsOnFrontPage: Boolean,
    override val sizeBytes: BigDecimal,
    override val fileName: String,
    override val storageCredential: UUID
) : AppBinaryDTO, java.io.Serializable

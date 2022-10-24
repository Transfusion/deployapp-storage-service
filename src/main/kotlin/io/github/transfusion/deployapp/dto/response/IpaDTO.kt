package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.JsonNode
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class IpaDTO(
    // IPA-specific fields
    val minSdkVersion: String,
    val iphone: Boolean,
    val ipad: Boolean,
    val universal: Boolean,
    val deviceType: String?,
    val archs: List<String>,
    val displayName: String?,
    val releaseType: String?,
    val buildType: String?,
    val devices: List<String>?,
    val teamName: String?,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val expiredDate: Instant?,
    val plistJson: JsonNode,

    // AppBinary fields
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
    override val storageCredential: UUID,
    override val description: String?
) : AppBinaryDTO, java.io.Serializable {
    override val type: String
        get() = "IPA"
}

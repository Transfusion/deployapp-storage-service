package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class ApkDTO(
    // APK-specific fields
    val minSdkVersion: String,
    val minOsVersion: String,
    val targetSdkVersion: String,
    val wear: Boolean,
    val tv: Boolean,
    val automotive: Boolean,
    val deviceType: String,
    val useFeatures: List<String>?,
    val usePermissions: List<String>?,
    val deepLinks: List<String>?,
    val schemes: List<String>?,
    val manifestXml: String,

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
    override val description: String?,

    override val organizationId: UUID?,
    override val userId: UUID?,

    override val available: Boolean,
    override val appBinaryStoreJob: AppBinaryStoreJobDTO?
) : AppBinaryDTO, java.io.Serializable {
    override val type: String
        get() = "APK"
}


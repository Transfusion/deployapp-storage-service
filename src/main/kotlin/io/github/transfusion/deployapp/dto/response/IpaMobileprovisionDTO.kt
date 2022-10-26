package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.util.*

data class IpaMobileprovisionDTO(
    val id: UUID,
    val name: String?,
    val appName: String?,
    val type: String?,
    val platform: String?,
    val teamName: String?,
    val profileName: String?,

    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val createdDate: Instant?,

    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val expiredDate: Instant?,
    val adhoc: Boolean,
    val development: Boolean,
    val enterprise: Boolean,
    val appstore: Boolean,
    val inhouse: Boolean,

    // omit the AppBinary for now

    val platforms: List<String>?,
    val devices: List<String>?,
    val team_identifier: List<String>?,
    val enabled_capabilities: List<String>?
)

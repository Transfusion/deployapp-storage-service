package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.util.*

data class AppBinaryDownloadDTO(
    val id: UUID,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val ts: Instant,
    val ip: String,
    val ua: String,

    val os: String?,
    val version: String?,
)

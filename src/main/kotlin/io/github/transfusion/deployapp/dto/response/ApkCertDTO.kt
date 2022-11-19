package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.util.*

data class ApkCertDTO(
    val id: UUID,
    val subject: String,
    val issuer: String,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val notBefore: Instant,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val notAfter: Instant,
    val path: String,
)

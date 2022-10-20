package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class AppBinaryJobDTO(
    val id: String,
    val appBinaryId: String,
    val name: String,
    val description: String?,
    @get:JsonFormat(shape = JsonFormat.Shape.STRING)
    val createdDate: Instant
)

package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.time.Instant
import java.util.*

data class AppBinaryStoreJobDTO(
        val id: UUID,
        val name: String,
        val status: String,
        @get:JsonFormat(shape = JsonFormat.Shape.STRING)
        val createdDate: Instant,
        val appBinaryId: UUID,
) : Serializable

package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.databind.JsonNode

data class AppBinaryAssetDTO(
    val id: String,

    val appBinaryId: String,

    val type: String,
    val status: String?,
    val fileName: String,
    val description: String?,

    val value: JsonNode?,
)

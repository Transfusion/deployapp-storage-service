package io.github.transfusion.deployapp.dto.response

import com.fasterxml.jackson.databind.JsonNode
import io.github.transfusion.deployapp.storagemanagementservice.services.assets.Constants.ASSET_STATUS

data class AppBinaryAssetDTO(
    val id: String,

    val appBinaryId: String,

    val type: String,
    val status: ASSET_STATUS?,
    val fileName: String?,
    val description: String?,

    val value: JsonNode?,
)

package io.github.transfusion.deployapp.dto.response

import java.util.*

data class AppBinaryAliasDTO(
    val alias: String,
    val appBinaryId: UUID
)
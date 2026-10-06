package org.bakasu.bakasu.domain.model

data class SuSFSStatus(
    val enabled: Boolean = false,
    val version: String = "",
    val enabledFeatures: String = "",
)

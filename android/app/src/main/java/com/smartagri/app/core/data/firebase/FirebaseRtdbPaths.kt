package com.smartagri.app.core.data.firebase

object FirebaseRtdbPaths {
    fun deviceOwner(deviceId: String): String = "deviceOwners/${segment(deviceId)}"
    fun access(viewerUid: String, deviceId: String): String =
        "access/${segment(viewerUid)}/${segment(deviceId)}"
    fun metadata(deviceId: String): String = "devices/${segment(deviceId)}/metadata"
    fun latest(deviceId: String): String = "devices/${segment(deviceId)}/latest"
    fun reading(deviceId: String, readingId: String): String =
        "readings/${segment(deviceId)}/${segment(readingId)}"

    private fun segment(value: String): String {
        require(value.isNotBlank()) { "Firebase path segment must not be blank" }
        require(FORBIDDEN.none(value::contains)) { "Invalid Firebase path segment" }
        return value
    }

    private val FORBIDDEN = charArrayOf('.', '#', '$', '[', ']', '/')
}

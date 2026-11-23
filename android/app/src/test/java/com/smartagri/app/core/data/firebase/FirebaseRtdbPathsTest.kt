package com.smartagri.app.core.data.firebase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FirebaseRtdbPathsTest {
    @Test
    fun `approved paths are stable`() {
        assertEquals("devices/device-1/latest", FirebaseRtdbPaths.latest("device-1"))
        assertEquals(
            "readings/device-1/reading-1",
            FirebaseRtdbPaths.reading("device-1", "reading-1"),
        )
        assertEquals("access/viewer-1/device-1", FirebaseRtdbPaths.access("viewer-1", "device-1"))
    }

    @Test
    fun `invalid path segment is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            FirebaseRtdbPaths.latest("device/other")
        }
    }
}

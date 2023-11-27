package fr.delcey.dailynino.stubs

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/**
 * Sunday 26/11/2023 - 22:56:23
 */
fun getDefaultClock(): Clock = Clock.fixed(
    getDefaultInstant(),
    ZoneOffset.UTC
)

/**
 * Sunday 26/11/2023 - 22:56:23
 */
fun getDefaultInstant(): Instant = Instant.ofEpochSecond(1701035783)
package io.eugenethedev.taigamobile.ui.utils

import kotlinx.coroutines.delay

/**
 * Sometimes little delay is needed to make animations work smooth
 */
suspend fun fixAnimation() = delay(300)

package io.eugenethedev.taigamobile.ui.screens.main

import androidx.compose.runtime.staticCompositionLocalOf
import java.io.InputStream

/**
 * Way to pick files from composables
 */
abstract class FilePicker {
    private var onFilePicked: (String, InputStream) -> Unit = { _, _ -> }

    open fun requestFile(onFilePicked: (String, InputStream) -> Unit) {
        this.onFilePicked = onFilePicked
    }

    fun filePicked(name: String, inputStream: InputStream) = onFilePicked(name, inputStream)
}

val LocalFilePicker = staticCompositionLocalOf<FilePicker> { error("No FilePicker provided") }

package io.eugenethedev.taigamobile.utils

import android.annotation.SuppressLint
import android.util.Log
import timber.log.Timber.DebugTree
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class FileLoggingTree(private val basePath: String, private val minPriority: Int) : DebugTree() {

    var currentFile: File? = null
        private set

    private var currentFileWriter: FileWriter? = null

    override fun isLoggable(tag: String?, priority: Int) = priority >= minPriority

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val fileNameTimeStamp: String = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())
            val logTimeStamp: String = SimpleDateFormat(
                "E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                Locale.getDefault()
            ).format(Date())
            val fileName = "$fileNameTimeStamp.log"

            // Create file
            currentFile?.takeIf { it.name == fileName } ?: run {
                currentFile = generateFile(basePath, fileName)
                currentFileWriter = FileWriter(currentFile, true)
            }

            // If file created or exists save logs
            currentFileWriter?.let {
                it.append(logTimeStamp)
                    .append(" : ")
                    .append(tag)
                    .append(" - ")
                    .append(message)
                    .append("\n\n")
                it.flush()
            }
        } catch (e: Exception) {
            Log.e(
                LOG_TAG,
                "Error while logging into file : $e"
            )
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }

    protected fun finalize() {
        currentFileWriter?.close()
    }

    companion object {
        private val LOG_TAG = FileLoggingTree::class.java.simpleName
        /*  Helper method to create file*/

        private fun generateFile(path: String, fileName: String): File? {
            var file: File? = null

            val root = File(path)
            var dirExists = true
            if (!root.exists()) {
                dirExists = root.mkdirs()
            }
            if (dirExists) {
                file = File(root, fileName)
            }

            return file
        }

    }
}

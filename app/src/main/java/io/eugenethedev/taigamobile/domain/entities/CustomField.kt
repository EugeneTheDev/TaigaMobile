package io.eugenethedev.taigamobile.domain.entities

import java.util.*


enum class CustomFieldType {
    Text,
    Multiline,
    RichText,
    Date,
    Url,
    Dropdown,
    Number
}

data class CustomField(
    val id: Long,
    val type: CustomFieldType,
    val name: String,
    val description: String?,
    val value: CustomFieldValue,
    val options: List<String>? = null // for CustomFieldType.Dropdown
)

@JvmInline
value class CustomFieldValue(private val value: Any) {
    init {
        require(
            value is String ||
            value is Date   ||
            value is Int
        )
    }

    val stringValue get() = value as? String ?: throw IllegalArgumentException("value is not String")
    val intValue get() = value as? Int ?: throw IllegalArgumentException("value is not Int")
    val dateValue get() = value as? Date ?: throw IllegalArgumentException("value is not Date")
}

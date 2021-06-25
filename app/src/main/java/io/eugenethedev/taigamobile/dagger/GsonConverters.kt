package io.eugenethedev.taigamobile.dagger

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.*
import java.time.format.DateTimeFormatter

class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(DateTimeFormatter.ISO_LOCAL_DATE.format(value))
    }

    override fun read(input: JsonReader): LocalDate = input.nextString().toLocalDate()
}

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.value(
            value.atZone(ZoneId.systemDefault())
                .toInstant()
                .toString()
        )
    }

    override fun read(input: JsonReader): LocalDateTime = Instant.parse(input.nextString())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

// used in TaskRepository
fun String.toLocalDate() = LocalDate.parse(this)


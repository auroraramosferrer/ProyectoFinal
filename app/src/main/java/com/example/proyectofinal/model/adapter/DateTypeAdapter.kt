import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.*

class DateTypeAdapter : TypeAdapter<Date>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun write(out: JsonWriter?, value: Date?) {
        value?.let {
            out?.value(dateFormat.format(it))
        }
    }

    override fun read(`in`: JsonReader?): Date {
        val dateString = `in`?.nextString()
        return try {
            dateString?.let {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                isoFormat.parse(it) ?: Date(0)
            } ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }
}

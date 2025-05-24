import android.content.Context
import com.example.proyectofinal.R
import java.util.*

object ConfigReader {
    private var properties: Properties? = null

    fun init(context: Context) {
        properties = Properties().apply {
            context.resources.openRawResource(R.raw.config).use { inputStream ->
                load(inputStream)
            }
        }
    }

    fun getProperty(key: String): String {
        return properties?.getProperty(key)
            ?: throw IllegalStateException("ConfigReader no inicializado o propiedad '$key' no encontrada")
    }
}
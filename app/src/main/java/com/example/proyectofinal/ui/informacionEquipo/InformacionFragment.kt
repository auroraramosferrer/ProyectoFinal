package com.example.proyectofinal.ui.informacionEquipo

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentInformacionBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class InformacionFragment : Fragment() {

    private var _binding: FragmentInformacionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInformacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong("id") ?: 0L
        val idaula = arguments?.getLong("idaula") ?: 0L

        if (id != 0L) {
            fetchEquipoPorId(id, idaula)
            comprobarVisibilidadIncidencias(id)
        } else {
            Log.e("InformacionFragment", "ID inválido: $id")
            Toast.makeText(requireContext(), "ID inválido", Toast.LENGTH_SHORT).show()
        }

        binding.fabEliminarEquipo.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás segur@ de que deseas eliminar este equipo?")
                .setPositiveButton("Sí") { dialog, _ ->
                    eliminarEquipo(id)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    Toast.makeText(requireContext(), "Cancelando operación", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .show()
        }
        binding.buttonIncidencias.setOnClickListener {
            findNavController().navigate(R.id.action_informacion_to_consultar_incidencia)
        }
        binding.fabImprimirQr.setOnClickListener {
            if (id != 0L) {
                val qrBitmap = generarCodigoQR(id.toString())
                if (qrBitmap != null) {
                    mostrarDialogoQR(qrBitmap)
                } else {
                    Toast.makeText(requireContext(), "No se pudo generar el código QR", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun fetchEquipoPorId(id: Long, idaulaArgument: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val equipo = RetrofitClient.apiService.getEquipoPorId(id)

                if (equipo != null) {
                    Log.d("InformacionFragment", "Equipo recibido: $equipo")

                    binding.textViewTextoAula.text = equipo.aula
                    binding.textViewTextoMarca.text = equipo.marca
                    binding.textViewTextoCodigo.text = equipo.codigo
                    binding.textViewTextoModelo.text = equipo.modelo
                    binding.textViewTextoProcesador.text = equipo.procesador
                    binding.textViewTextoCPU.text = equipo.cpu
                    binding.textViewTextoGrafica.text = equipo.grafica
                    binding.textViewTextoRam.text = "${equipo.ram}GB"
                    binding.textViewTextoBluetooth.text = if (equipo.bluetooth) "Sí" else "No"
                    binding.textViewTextoWifi.text = if (equipo.wifi) "Sí" else "No"
                    binding.textViewTextoNDiscos.text = equipo.ndiscos.toString()
                    binding.textViewTextoTipoDiscos.text = equipo.tipodiscos
                    binding.textViewTextoSO.text = equipo.so
                    binding.imageViewOrdenador.setImageBitmap(base64ToBitmap(equipo.foto))

                    val aulaNombre = equipo.aula.trim()

                    fetchIdAulaPorNombre(aulaNombre) { idaulaObtenido ->

                        binding.fabIncidencia.setOnClickListener {
                            val bundle = Bundle().apply {
                                putInt("idaula", idaulaObtenido)
                                putLong("id", id)
                            }
                            findNavController().navigate(R.id.action_informacion_to_crear_incidencia, bundle)
                        }

                        binding.fabEditarEquipo.setOnClickListener {
                            val bundle = Bundle().apply {
                                equipo.id?.let { it1 -> putLong("id", it1) }
                                putString("codigo", equipo.codigo)
                                putString("aula", equipo.aula)
                                putString("marca", equipo.marca)
                                putString("modelo", equipo.modelo)
                                putString("procesador", equipo.procesador)
                                putString("cpu", equipo.cpu)
                                putString("grafica", equipo.grafica)
                                putInt("ram", equipo.ram)
                                putString("so", equipo.so)
                                putInt("ndiscos", equipo.ndiscos)
                                putString("tipodiscos", equipo.tipodiscos)
                                putString(
                                    "bluetooth",
                                    if (equipo.bluetooth) "true" else "false"
                                )  // Enviar true/false como String
                                putString("wifi", if (equipo.wifi) "true" else "false")
                                putString("foto", equipo.foto ?: "")
                                putLong("idaula", idaulaObtenido.toLong())
                            }

                            findNavController().navigate(R.id.action_informacion_to_editar_equipo, bundle)
                        }
                    }

                } else {
                    Log.e("InformacionFragment", "No se encontró equipo con ID: $id")
                    Toast.makeText(requireContext(), "No se encontraron datos del equipo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar los datos del equipo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchIdAulaPorNombre(aulaNombre: String, callback: (Int) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val aula = RetrofitClient.apiService.getAulaPorNombre(aulaNombre)
                if (aula != null) {
                    aula.id?.let { callback(it.toInt()) }
                } else {
                    Log.e("InformacionFragment", "No se encontró el aula: $aulaNombre")
                    Toast.makeText(requireContext(), "Aula no encontrada", Toast.LENGTH_SHORT).show()
                    callback(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al obtener el ID del aula", Toast.LENGTH_SHORT).show()
                callback(0)
            }
        }
    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            val tempFile = File(context?.cacheDir, "temp_image.jpg")
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            val correctedBitmap = correctImageOrientation(bitmap, tempFile.absolutePath)
            tempFile.delete()  // Eliminamos el archivo temporal

            correctedBitmap
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        try {
            val exif = android.media.ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_NORMAL
            )

            return when (orientation) {
                android.media.ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                android.media.ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                android.media.ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipBitmap(bitmap, true, false)
                android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipBitmap(bitmap, false, true)
                else -> bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flipBitmap(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun eliminarEquipo(id: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.apiService.eliminarEquipo(id)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Equipo eliminado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar el equipo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al eliminar el equipo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun comprobarVisibilidadIncidencias(idEquipo: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val incidencias = RetrofitClient.apiService.getIncidenciasPorEquipo(idEquipo)
                if (incidencias.isNotEmpty()) {
                    binding.viewContenedorIncidencias.visibility = View.VISIBLE
                    binding.textViewIncidenciasActivas.visibility = View.VISIBLE
                    binding.buttonIncidencias.visibility = View.VISIBLE
                } else {
                    binding.viewContenedorIncidencias.visibility = View.GONE
                    binding.textViewIncidenciasActivas.visibility = View.GONE
                    binding.buttonIncidencias.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, ocultamos el contenedor por defecto
                binding.viewContenedorIncidencias.visibility = View.GONE
            }
        }
    }

    private fun generarCodigoQR(texto: String, tamano: Int = 500): Bitmap? {
        return try {
            val writer = com.google.zxing.qrcode.QRCodeWriter()
            val bitMatrix = writer.encode(texto, com.google.zxing.BarcodeFormat.QR_CODE, tamano, tamano)
            val bitmap = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.RGB_565)

            for (x in 0 until tamano) {
                for (y in 0 until tamano) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mostrarDialogoQR(qrBitmap: Bitmap) {
        val imageView = ImageView(requireContext()).apply {
            setImageBitmap(qrBitmap)
            adjustViewBounds = true
            maxWidth = 600
            maxHeight = 600
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Código QR")
            .setView(imageView)
            .setPositiveButton("Imprimir") { dialog, _ ->
                qrBitmap?.let { imprimirBitmap(it) }
                dialog.dismiss()
            }

            .setNegativeButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun obtenerDispositivoBluetooth(nombreDispositivo: String = "mpt-II"): BluetoothDevice? {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth no está disponible", Toast.LENGTH_SHORT).show()
            return null
        }
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(requireContext(), "Bluetooth está desactivado", Toast.LENGTH_SHORT).show()
            return null
        }
        val dispositivosEmparejados = bluetoothAdapter.bondedDevices
        dispositivosEmparejados.forEach {
            if (it.name.equals(nombreDispositivo, ignoreCase = true)) {
                return it
            }
        }
        Toast.makeText(requireContext(), "No se encontró impresora Bluetooth '$nombreDispositivo'", Toast.LENGTH_SHORT)
            .show()
        return null
    }

    private var bluetoothSocket: BluetoothSocket? = null

    private fun conectarImpresora(device: BluetoothDevice): Boolean {
        return try {
            val uuid = device.uuids.firstOrNull()?.uuid ?: UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al conectar con la impresora", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun desconectarImpresora() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun imprimirQRCode(texto: String) {
        val device = obtenerDispositivoBluetooth() ?: return
        if (!conectarImpresora(device)) return

        try {
            val outputStream: OutputStream? = bluetoothSocket?.outputStream
            if (outputStream == null) {
                Toast.makeText(requireContext(), "No se pudo obtener el canal de impresión", Toast.LENGTH_SHORT).show()
                return
            }

            val data = buildEscPosQrCommand(texto)
            outputStream.write(data)

            // Avanza varias líneas para que el ticket no quede cortado
            val advancePaper = byteArrayOf(0x1B, 0x64, 0x04) // ESC d 4 líneas
            outputStream.write(advancePaper)

            outputStream.flush()

            Toast.makeText(requireContext(), "Impresión enviada", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al enviar datos a la impresora", Toast.LENGTH_SHORT).show()
        } finally {
            desconectarImpresora()
        }
    }

    private fun buildEscPosQrCommand(text: String): ByteArray {
        val qrData = text.toByteArray(Charsets.UTF_8)
        val length = qrData.size + 3
        val pL = (length and 0xFF).toByte()
        val pH = ((length shr 8) and 0xFF).toByte()

        val output = ByteArrayOutputStream()

        // 1) Establecer modelo QR (modelo 2)
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00))

        // 2) Establecer tamaño del módulo (por ejemplo 8)
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x08))

        // 3) Establecer nivel de corrección de errores (48 = nivel L)
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))

        // 4) Guardar datos QR
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30))
        output.write(qrData)

        // 5) Imprimir código QR
        output.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))

        return output.toByteArray()
    }

    fun bitmapToEscPosCommand(bitmap: Bitmap): ByteArray {
        val scaledBitmap = scaleBitmapForPrint(bitmap)
        val width = scaledBitmap.width
        val height = scaledBitmap.height
        val bytesPerLine = width / 8 + if (width % 8 > 0) 1 else 0

        val output = ByteArrayOutputStream()

        output.write(byteArrayOf(0x1D, 0x76, 0x30, 0x00))
        output.write((bytesPerLine and 0xFF))
        output.write(((bytesPerLine shr 8) and 0xFF))
        output.write((height and 0xFF))
        output.write(((height shr 8) and 0xFF))

        for (y in 0 until height) {
            for (x in 0 until bytesPerLine) {
                var byte = 0
                for (bit in 0..7) {
                    val pixelX = x * 8 + bit
                    if (pixelX < width) {
                        val pixel = scaledBitmap.getPixel(pixelX, y)
                        // Convertir a blanco y negro simple
                        val luminance =
                            (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114)
                        val isBlack = luminance < 127
                        if (isBlack) {
                            byte = byte or (1 shl (7 - bit))
                        }
                    }
                }
                output.write(byte)
            }
        }
        return output.toByteArray()
    }

    private fun isPixelBlack(pixel: Int): Boolean {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        // Usamos una fórmula simple para decidir si es negro o blanco
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b)
        return luminance < 127
    }

    private fun imprimirBitmap(bitmap: Bitmap) {
        val device = obtenerDispositivoBluetooth() ?: return
        if (!conectarImpresora(device)) return

        try {
            val outputStream: OutputStream? = bluetoothSocket?.outputStream
            if (outputStream == null) {
                Toast.makeText(requireContext(), "No se pudo obtener el canal de impresión", Toast.LENGTH_SHORT).show()
                return
            }

            val data = bitmapToEscPosCommand(bitmap)
            outputStream.write(data)

            // Avanzar papel unas líneas
            outputStream.write(byteArrayOf(0x1B, 0x64, 0x03)) // ESC d 3 líneas

            outputStream.flush()

            Toast.makeText(requireContext(), "Impresión enviada", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al enviar datos a la impresora", Toast.LENGTH_SHORT).show()
        } finally {
            desconectarImpresora()
        }
    }

    fun scaleBitmapForPrint(bitmap: Bitmap, targetWidth: Int = 360): Bitmap {
        val aspectRatio = bitmap.height.toDouble() / bitmap.width.toDouble()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
    }

}

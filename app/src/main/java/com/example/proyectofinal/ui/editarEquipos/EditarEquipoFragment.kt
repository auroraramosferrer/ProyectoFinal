package com.example.proyectofinal.ui.editarEquipos

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentEditarEquipoBinding
import com.example.proyectofinal.model.Equipo
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class EditarEquipoFragment : Fragment() {

    private lateinit var binding: FragmentEditarEquipoBinding
    private val viewModel: EditarEquipoViewModel by viewModels()
    private var id: Long = -1L
    private var idaula: Long = 0L
    private var currentPhotoPath: String? = null
    private var imageBase64: String = ""
    private var selectedImageUri: Uri? = null
    private var nombreAula: String = ""

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { handleImageUri(it) } }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && selectedImageUri != null) {
            handleImageUri(selectedImageUri!!)
        }
    }

    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) openGallery() else showPermissionDeniedToast() }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) openCamera() else showPermissionDeniedToast() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarEquipoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        imageBase64 = args?.getString("foto") ?: "" // Asignar el valor correctamente
        if (imageBase64.isNotEmpty()) {
            val bitmap = base64ToBitmap(imageBase64)
            if (bitmap != null) {
                binding.includeImagen.imageButtonAdjuntar.setImageBitmap(bitmap)
                binding.includeImagen.imageButtonCamara.setImageBitmap(bitmap)
            }
        }

        id = args?.getLong("id") ?: -1
        idaula = args?.getLong("idaula") ?: 0

        binding.includeLocalizacion.aula.setText(args?.getString("aula") ?: "")
        nombreAula = args?.getString("aula") ?: ""
        binding.includeLocalizacion.codigo.setText(args?.getString("codigo") ?: "")

        binding.includeHardware.marca.setText(args?.getString("marca") ?: "")
        binding.includeHardware.modelo.setText(args?.getString("modelo") ?: "")
        binding.includeHardware.procesador.setText(args?.getString("procesador") ?: "")
        binding.includeHardware.cpu.setText(args?.getString("cpu") ?: "")
        binding.includeHardware.ram.setText((args?.getInt("ram") ?: 0).toString())
        binding.includeHardware.ndiscos.setText((args?.getInt("ndiscos") ?: 0).toString())
        binding.includeHardware.tipodiscos.setText(args?.getString("tipodiscos") ?: "")
        binding.includeHardware.tarjeta.setText(args?.getString("grafica") ?: "")
        binding.includeHardware.switchBluetooth.isChecked = args?.getString("bluetooth")?.toBoolean() ?: false
        binding.includeHardware.switchWifi.isChecked = args?.getString("wifi")?.toBoolean() ?: false

        binding.includeSoftware.so.setText(args?.getString("so") ?: "")

        binding.fabCompletarEquipo.setOnClickListener {
            if (validarCampos()) {
                actualizarEquipo()
            }
        }

        binding.includeImagen.imageButtonAdjuntar.setOnClickListener {
            checkGalleryPermission()
        }

        binding.includeImagen.imageButtonCamara.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun actualizarEquipo() {
        if (imageBase64.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, añade una imagen del equipo", Toast.LENGTH_SHORT).show()
            return
        }

        val codigo = binding.includeLocalizacion.codigo.text.toString().trim()
        val marca = binding.includeHardware.marca.text.toString().trim()
        val modelo = binding.includeHardware.modelo.text.toString().trim()
        val procesador = binding.includeHardware.procesador.text.toString().trim()
        val ram = binding.includeHardware.ram.text.toString().trim().toIntOrNull() ?: 0
        val cpu = binding.includeHardware.cpu.text.toString().trim()
        val ndiscos = binding.includeHardware.ndiscos.text.toString().trim().toIntOrNull() ?: 0
        val tipodiscos = binding.includeHardware.tipodiscos.text.toString().trim()
        val grafica = binding.includeHardware.tarjeta.text.toString().trim()
        val bluetooth = binding.includeHardware.switchBluetooth.isChecked
        val wifi = binding.includeHardware.switchWifi.isChecked
        val so = binding.includeSoftware.so.text.toString().trim()

        val equipoActualizado = Equipo(
            id = id,
            codigo = codigo,
            marca = marca,
            modelo = modelo,
            procesador = procesador,
            ram = ram,
            cpu = cpu,
            ndiscos = ndiscos,
            tipodiscos = tipodiscos,
            grafica = grafica,
            bluetooth = bluetooth,
            wifi = wifi,
            so = so,
            foto = imageBase64,
            aula = nombreAula
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.actualizarEquipo(id, equipoActualizado)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Equipo actualizado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun validarCampos(): Boolean {
        var valido = true
        if (binding.includeLocalizacion.codigo.text.isNullOrEmpty()) {
            binding.includeLocalizacion.codigo.error = "Campo obligatorio"
            valido = false
        }
        if (binding.includeHardware.marca.text.isNullOrEmpty()) {
            binding.includeHardware.marca.error = "Campo obligatorio"
            valido = false
        }
        if (binding.includeSoftware.so.text.isNullOrEmpty()) {
            binding.includeSoftware.so.error = "Campo obligatorio"
            valido = false
        }
        return valido
    }

    private fun checkGalleryPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> openGallery()

            else -> requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> openCamera()

            else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }

    private fun openCamera() {
        createImageFile()?.let { uri ->
            selectedImageUri = uri
            takePicture.launch(uri)
        }
    }

    private fun createImageFile(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireContext().externalCacheDir
            val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }

            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al crear el archivo", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun handleImageUri(uri: Uri) {
        try {
            val bitmap = uriToBitmap(uri)
            bitmap?.let {
                imageBase64 = bitmapToBase64(it)
                binding.includeImagen.imageButtonAdjuntar.setImageBitmap(it)
                binding.includeImagen.imageButtonCamara.setImageBitmap(it)
                Log.d("ImageDebug", "Base64 length: ${imageBase64.length}")
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream).also {
                inputStream?.close()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(requireContext(), "Permiso denegado. No puedes usar esta funcionalidad.", Toast.LENGTH_SHORT)
            .show()
    }

    private fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            null
        }
    }
}

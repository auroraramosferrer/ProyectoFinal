package com.example.proyectofinal.ui.registrarEquipo

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
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentRegistrarBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class RegistrarFragment : Fragment() {

    private var _binding: FragmentRegistrarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegistrarViewModel by viewModels()
    private var currentPhotoPath: String? = null
    private var imageBase64: String = ""
    private var selectedImageUri: Uri? = null


    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageUri(it) }
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoPath != null) {
            selectedImageUri?.let { handleImageUri(it) }
        }
    }


    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) openGallery() else showPermissionDeniedToast() }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) openCamera() else showPermissionDeniedToast() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrarBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        binding.floatingActionButtonLeft.setOnClickListener {
            findNavController().navigate(R.id.action_registrar_to_home)
        }

        binding.floatingActionButtonRight.setOnClickListener {
            val aulaNombre = getTextFromInputLayout(binding.includeLocalizacion.aulaInputLayout)
            val codigo = getTextFromInputLayout(binding.includeLocalizacion.codigoInputLayout)

            if (aulaNombre.isEmpty()) {
                binding.includeLocalizacion.aulaInputLayout.error = "Introduce un aula"
                return@setOnClickListener
            } else {
                binding.includeLocalizacion.aulaInputLayout.error = null
            }

            if (codigo.isEmpty()) {
                binding.includeLocalizacion.codigoInputLayout.error = "Introduce un código"
                return@setOnClickListener
            } else {
                binding.includeLocalizacion.codigoInputLayout.error = null
            }

            if (imageBase64.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, añade una imagen del equipo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            comprobarAulaYRegistrar(aulaNombre)
        }


        binding.includeImagen.imageButtonAdjuntar.setOnClickListener {
            checkGalleryPermission()
        }

        binding.includeImagen.imageButtonCamara.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val aula = getTextFromInputLayout(binding.includeLocalizacion.aulaInputLayout)
        val codigo = getTextFromInputLayout(binding.includeLocalizacion.codigoInputLayout)

        if (aula.isEmpty()) {
            binding.includeLocalizacion.aulaInputLayout.error = "Introduce un aula"
            isValid = false
        } else {
            binding.includeLocalizacion.aulaInputLayout.error = null
        }

        if (codigo.isEmpty()) {
            binding.includeLocalizacion.codigoInputLayout.error = "Introduce un código"
            isValid = false
        } else {
            binding.includeLocalizacion.codigoInputLayout.error = null
        }

        if (imageBase64.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, añade una imagen del equipo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


    private fun collectDataAndSave() {
        try {
            val codigo = getTextFromInputLayout(binding.includeLocalizacion.codigoInputLayout)
            val aula = getTextFromInputLayout(binding.includeLocalizacion.aulaInputLayout)
            val marca = getTextFromInputLayout(binding.includeHardware.marcaInputLayout)
            val modelo = getTextFromInputLayout(binding.includeHardware.modeloInputLayout)
            val procesador = getTextFromInputLayout(binding.includeHardware.procesadorInputLayout)
            val ram = getTextFromInputLayout(binding.includeHardware.ramInputLayout).toIntOrNull() ?: 0
            val cpu = getTextFromInputLayout(binding.includeHardware.cpuInputLayout)
            val ndiscos = getTextFromInputLayout(binding.includeHardware.ndiscosInputLayout).toIntOrNull() ?: 0
            val tipodiscos = getTextFromInputLayout(binding.includeHardware.tipodiscosInputLayout)
            val grafica = getTextFromInputLayout(binding.includeHardware.tarjetaInputLayout)
            val bluetooth = binding.includeHardware.switchBluetooth.isChecked
            val wifi = binding.includeHardware.switchWifi.isChecked
            val so = getTextFromInputLayout(binding.includeSoftware.soInputLayout)

            if (imageBase64.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, añade una imagen del equipo", Toast.LENGTH_SHORT).show()
                return
            }

            viewModel.insertarEquipos(
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
                codigo = codigo,
                aula = aula,
                so = so,
                imageBase64 = imageBase64
            )

            Toast.makeText(requireContext(), "Equipo insertado con éxito", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registrar_to_home)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            Log.e("RegistrarFragment", "Error al guardar equipo", e)
        }
    }

    private fun getTextFromInputLayout(textInputLayout: TextInputLayout): String {
        return textInputLayout.editText?.text?.toString()?.trim() ?: ""
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
        createImageFile()?.let { photoUri ->
            selectedImageUri = photoUri
            takePicture.launch(photoUri)
        }
    }

    private fun createImageFile(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireContext().externalCacheDir
            val imageFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                currentPhotoPath = absolutePath
            }

            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile
            )
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), "Error al crear el archivo", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun handleImageUri(uri: Uri) {
        try {
            val bitmap = uriToBitmap(uri)
            bitmap?.let {
                // Corregir la orientación de la imagen
                val correctedBitmap = correctImageOrientation(it, uri)

                imageBase64 = bitmapToBase64(correctedBitmap)
                binding.includeImagen.imageButtonAdjuntar.setImageBitmap(correctedBitmap)
                binding.includeImagen.imageButtonCamara.setImageBitmap(correctedBitmap)
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

    private fun correctImageOrientation(bitmap: Bitmap, imageUri: Uri): Bitmap {
        try {
            val exif = android.media.ExifInterface(imageUri.path!!)
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

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(
            requireContext(),
            "Permiso denegado. No puedes usar esta funcionalidad.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun comprobarAulaYRegistrar(nombreAula: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val aula = RetrofitClient.apiService.getAulaPorNombre(nombreAula)
                if (aula != null) {
                    if (validateInputs()) {
                        collectDataAndSave()
                    }
                } else {
                    Toast.makeText(requireContext(), "El aula '$nombreAula' no existe", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "El aula '$nombreAula' no existe", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

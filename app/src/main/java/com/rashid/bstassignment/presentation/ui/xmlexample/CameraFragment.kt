package com.rashid.bstassignment.presentation.ui.xmlexample

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.databinding.CameraFragmentBinding
import com.rashid.bstassignment.presentation.viewmodels.ImageUpdateVM
import com.rashid.bstassignment.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private lateinit var cameraFragmentBinding: CameraFragmentBinding
    private var imageUri: Uri? = null
    private val imageUploadVm: ImageUpdateVM by viewModels()

    private var imageCapture: ImageCapture? = null
    private var apiRequestJob: Job? = null
    private lateinit var userImages: UserImages

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with camera usage
            startCamera()
        } else {
            // Permission denied
            Toast.makeText(requireContext(), "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraFragmentBinding = CameraFragmentBinding.inflate(inflater, container, false)
        val view = cameraFragmentBinding.root
        userImages = UserImages()
        isNetworkConnected()
        if (isCameraPermissionGranted()
        ) {
            startCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        cameraFragmentBinding.captureImgCV.setOnClickListener {
            // Capture every second
            val captureIntervalMs = 5000L
            val executor = Executors.newSingleThreadScheduledExecutor()

            executor.scheduleAtFixedRate({
                takePhoto()
            }, 0, captureIntervalMs, TimeUnit.MILLISECONDS)

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerRegistered()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = cameraFragmentBinding.previewView.surfaceProvider
            }

            imageCapture =
                ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetResolution(Size(640, 480)).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoDir = requireContext().externalMediaDirs.firstOrNull()
            ?: requireContext().getExternalFilesDir(null)
        if (photoDir == null) {
            Toast.makeText(requireContext(), "Unable to access storage", Toast.LENGTH_SHORT).show()
            return
        }

        val photoFile = File(
            photoDir, SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    imageUri = savedUri
                    startApiRequest()
                    Toast.makeText(
                        requireContext(),
                        "Photo saved successfully=$savedUri",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun startApiRequest() {
        apiRequestJob = lifecycleScope.launch {
            withTimeoutOrNull(10_000L) {
                try {
                    uploadImageToServer()
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    private fun cancelApiRequest() {
        apiRequestJob?.cancel()
        apiRequestJob = null
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImageToServer() {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 320, 240, true)

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val imageData = outputStream.toByteArray()
        val file = File(imageUri?.path!!)
        userImages.isUploaded = 0
        userImages.imagePath = file.path.toString()
        userImages.imageName = file.name
        saveImageToLocalDb()
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)
        imageUploadVm.imagePostToServer(file.path,body)
    }

    private fun observerRegistered() {
        imageUploadVm.imageState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun saveImageToLocalDb() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    imageUploadVm.saveImageToDb(userImages)
                }
                Log.d("CameraFragment", "Image saved successfully")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Error saving image", e)
            }
        }

    }

    private fun updateImageToLocalDb() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    imageUploadVm.updateImageToDb(userImages)
                }
                Log.d("CameraFragment", "Image updated successfully")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Error updating image", e)
            }
        }

    }

    private fun handleStateChange(apiState: ImageUpdateVM.ImageUploadingState) {
        when (apiState) {
            is ImageUpdateVM.ImageUploadingState.Init -> Unit
            is ImageUpdateVM.ImageUploadingState.Error -> handleError(apiState.rawResponse)
            is ImageUpdateVM.ImageUploadingState.Success -> handleSuccess(apiState.imagePostResponse)
            is ImageUpdateVM.ImageUploadingState.Loading -> handleLoading(apiState.isloading)
            is ImageUpdateVM.ImageUploadingState.Exception -> handleException(apiState.exception)

        }
    }

    private fun handleException(exception: Any) {
        cameraFragmentBinding.progressBar.visibility = View.GONE
        Log.d("TAG", "handleException: $exception")
        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show()
    }

    private fun handleError(response: Any) {
        cameraFragmentBinding.progressBar.visibility = View.GONE
        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show()
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading)
            cameraFragmentBinding.progressBar.visibility = View.VISIBLE
        else
            cameraFragmentBinding.progressBar.visibility = View.GONE
    }

    private fun handleSuccess(imageResponse: ImgServerUploadResp) {
        if (imageResponse.statusCode == Constants.STATUS_CODE) {
            if (imageResponse.responseData.picture_draft?.isNotEmpty() == true) {
                userImages.isUploaded = 1
                updateImageToLocalDb()
                Toast.makeText(
                    requireContext(),
                    "Image successfully posted to server.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        } else {
            Toast.makeText(
                requireContext(),
                "Something went wrong.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isNetworkConnected() {
        lifecycleScope.launch {
            imageUploadVm.isNetworkConnected.collect { isConnected ->
                if (isConnected) {
                    if(imageUploadVm.getImageStatus(userImages.imageName.toString()) == "Pending"){
                        startApiRequest()
                    }else{
                        // notify user about internet is available
                    }

                } else {
                    cameraFragmentBinding.progressBar.visibility = View.GONE
                    cancelApiRequest()
                }
            }
        }
    }


}
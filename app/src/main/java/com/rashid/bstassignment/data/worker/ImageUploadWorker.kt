package com.rashid.bstassignment.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rashid.bstassignment.domain.repository.ImagePostRepo
import com.rashid.bstassignment.utils.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import javax.inject.Inject



@HiltWorker
class ImageUploadWorker  @AssistedInject constructor(
     @Assisted context: Context,
     @Assisted workerParams: WorkerParameters,
    private val repository: ImagePostRepo
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingImages = repository.getPendingImages()
        pendingImages.collect{images ->
            images.forEach { image ->
                try {
                    val imageFile = File(image.imagePath.toString())
                    repository.uploadImageFileWM(imageFile,Constants.BEARER_TOKEN)
                } catch (e: Exception) {
                    // Keep the pending image if it fails
                    e.printStackTrace()
                }
            }
        }

        return Result.success()
    }


}

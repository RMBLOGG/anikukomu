package com.dayynime.anikukomu.core

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object CloudinaryService {

    private val client = OkHttpClient()

    suspend fun uploadImage(context: Context, imageUri: Uri): CloudinaryResult = withContext(Dispatchers.IO) {
        val url = "https://api.cloudinary.com/v1_1/${Constants.CLOUDINARY_CLOUD_NAME}/image/upload"

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $imageUri")
        val imageBytes = inputStream.use { it.readBytes() }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "upload.jpg",
                imageBytes.toRequestBody("image/jpeg".toMediaType())
            )
            .addFormDataPart("upload_preset", Constants.CLOUDINARY_UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Cloudinary upload failed: ${response.code} - ${response.message}")
            }
            val bodyString = response.body?.string() ?: throw IllegalStateException("Empty response body from Cloudinary")
            val json = JSONObject(bodyString)
            CloudinaryResult(
                secureUrl = json.getString("secure_url"),
                publicId = json.getString("public_id")
            )
        }
    }
}

data class CloudinaryResult(val secureUrl: String, val publicId: String)

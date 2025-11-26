package com.example.pge.data.respositorios

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ConsumoRepositorio {
    fun uriToMultipart(context: Context, uri: Uri, name: String = "archivo"): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody(
            contentResolver.getType(uri)?.toMediaTypeOrNull()
        )
        return MultipartBody.Part.createFormData("archivo", "archivo.csv", requestBody)
    }
}
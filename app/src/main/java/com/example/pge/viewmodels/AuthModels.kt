package com.example.pge.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.preferences.TokenManager
import com.example.pge.data.respositorios.AutenticadorRepo
import com.example.pge.models.UserResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {

    private val repo = AutenticadorRepo(context)
    private val tokenManager = TokenManager(context)

    var user by mutableStateOf<UserResponse?>(null)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    // Se llama al inicio de la app para verificar si el token es válido
    fun checkSession() {
        viewModelScope.launch {
            val token = tokenManager.getToken()

            if (token == null) {
                isLoggedIn = false
                return@launch
            }

            try {
                val response = repo.getUser()

                if (response.isSuccessful && response.body() != null) {
                    user = response.body()
                    isLoggedIn = true
                } else {
                    tokenManager.clearToken()
                    isLoggedIn = false
                }

            } catch (e: Exception) {
                isLoggedIn = false
            }
        }
    }

    fun login(email: String, password: String, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repo.login(email, password)

                if (response.isSuccessful && response.body() != null) {

                    val token = response.body()!!.accessToken
                    tokenManager.saveToken(token)

                    checkSession()  // carga al usuario y marca como logueado

                } else {
                    onError()
                }
            } catch (e: Exception) {
                onError()
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        user = null
        isLoggedIn = false
    }
}



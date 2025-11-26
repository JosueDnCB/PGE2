package com.example.pge.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.LoginApi
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.data.preferences.TokenManager
import com.example.pge.models.LoginRequest
import com.example.pge.models.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : ViewModel() {

    private val tokenManager = TokenManager(context)
    private val loginApi = RetrofitInstance.getRetrofit(context).create(LoginApi::class.java)

    // 🔥 Estado observable del usuario
    private val _usuario = MutableStateFlow<UserResponse?>(null)
    val usuario: StateFlow<UserResponse?> get() = _usuario

    // 🔥 Estado observable de login
    private val _isLoggedIn = MutableStateFlow(tokenManager.getToken() != null)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn


    // --------------------------
    //  LOGIN
    // --------------------------
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Enviamos el request con el campo "contrasena", que es lo que tu API espera
                val response = loginApi.login(LoginRequest(email = email, contrasena = password))

                if (response.isSuccessful && response.body() != null) {
                    val loginBody = response.body()!!
                    val token = loginBody.accessToken

                    // Guardamos el token en TokenManager
                    tokenManager.saveToken(token)

                    // Actualizamos estado de login
                    _isLoggedIn.value = true

                    // Obtenemos los datos del usuario
                    getUser()

                    onResult(true)

                } else {
                    // Si la API devuelve un error, mostramos el mensaje real
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginError", errorBody ?: "Error desconocido del servidor")
                    onResult(false)
                }

            } catch (e: Exception) {
                // Capturamos cualquier excepción de red o JSON
                Log.e("LoginException", e.message ?: "Excepción desconocida")
                onResult(false)
            }
        }
    }


    // --------------------------
    //  GET USER ( /auth/me )
    // --------------------------
    fun getUser() {
        viewModelScope.launch {
            try {
                val response = loginApi.getUser()

                if (response.isSuccessful && response.body() != null) {

                    _usuario.value = response.body()!!   // Guardamos usuario en StateFlow ✔
                }

            } catch (_: Exception) { }
        }
    }

    fun cerrarSesion() {
        _isLoggedIn.value = false
        _usuario.value = null
    }

}


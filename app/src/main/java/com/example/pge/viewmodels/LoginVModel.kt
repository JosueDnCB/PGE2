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

    private val _usuario = MutableStateFlow<UserResponse?>(null)
    val usuario: StateFlow<UserResponse?> get() = _usuario

    private val _isLoggedIn = MutableStateFlow(tokenManager.getToken() != null)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    init {
        // Si hay token guardado, cargar usuario
        if (_isLoggedIn.value) {
            getUser()
        }
    }

    // --------------------------
    //  LOGIN
    // --------------------------
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = loginApi.login(LoginRequest(email = email, contrasena = password))

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.accessToken

                    tokenManager.saveToken(token)
                    _isLoggedIn.value = true

                    getUser()

                    onResult(true)

                } else {
                    Log.e("LoginError", response.errorBody()?.string() ?: "Error desconocido del servidor")
                    onResult(false)
                }

            } catch (e: Exception) {
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
                    _usuario.value = response.body()
                } else {
                    // Si el token NO sirve, cerrar sesión automáticamente
                    cerrarSesion()
                }

            } catch (_: Exception) {
                cerrarSesion()
            }
        }
    }

    // --------------------------
    //  LOGOUT
    // --------------------------
    fun cerrarSesion() {
        tokenManager.clearToken()      // ❗ NECESARIO
        _isLoggedIn.value = false
        _usuario.value = null
        Log.d("Logout", "Sesión cerrada correctamente")
    }
}

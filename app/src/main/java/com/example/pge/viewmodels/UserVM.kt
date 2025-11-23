package com.example.pge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.network.LoginApi
import com.example.pge.data.network.RetrofitInstance
import com.example.pge.data.preferences.TokenManager
import com.example.pge.models.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(context: Context) : ViewModel() {

    private val tokenManager = TokenManager(context)
    private val loginApi = RetrofitInstance.getRetrofit(context).create(LoginApi::class.java)

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user

    private val _isLoggedIn = MutableStateFlow(tokenManager.getToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        if (_isLoggedIn.value) {
            fetchUser()
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val response = loginApi.getUser()
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _user.value = null
                }
            } catch (e: Exception) {
                _user.value = null
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _isLoggedIn.value = false
        _user.value = null
    }
}

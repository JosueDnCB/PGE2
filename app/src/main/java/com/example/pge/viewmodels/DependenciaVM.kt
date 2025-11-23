package com.example.pge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pge.data.repositorios.DependenciasRepo
import com.example.pge.models.Dependencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DependenciasViewModel(context: Context) : ViewModel() {

    private val repo = DependenciasRepo(context)

    private val _dependencias = MutableStateFlow<List<Dependencia>>(emptyList())
    val dependencias: StateFlow<List<Dependencia>> = _dependencias

    fun cargarDependencias() {
        viewModelScope.launch {
            try {
                val lista = repo.getDependencias() // devuelve List<Dependencia>
                _dependencias.value = lista
            } catch (e: Exception) {
                _dependencias.value = emptyList()
            }
        }
    }
}

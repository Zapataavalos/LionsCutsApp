package com.example.applionscuts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.applionscuts.data.local.purchase.PurchaseEntity
import com.example.applionscuts.data.repository.PurchaseRepository
import kotlinx.coroutines.launch

class PurchaseViewModel(
    private val repo: PurchaseRepository
) : ViewModel() {

    // ------------------------------------------------------------
    // 🔥 LiveData para mostrar compras e información al usuario
    // ------------------------------------------------------------
    private val _purchaseHistory = MutableLiveData<List<PurchaseEntity>>()
    val purchaseHistory: LiveData<List<PurchaseEntity>> = _purchaseHistory

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message


    // ------------------------------------------------------------
    // 🔥 Al iniciar, cargar todas las compras del sistema
    // ------------------------------------------------------------
    init {
        loadPurchaseHistory()
    }


    // ------------------------------------------------------------
    // 🔥 Cargar todas las compras (modo administrador)
    // ------------------------------------------------------------
    fun loadPurchaseHistory() {
        viewModelScope.launch {
            try {
                _purchaseHistory.value = repo.getAllPurchases()
            } catch (e: Exception) {
                _message.value = "Error al cargar compras"
            }
        }
    }


    // ------------------------------------------------------------
    // 🔥 Registrar una nueva compra
    // ------------------------------------------------------------
    fun savePurchase(
        userId: Int,
        userName: String,
        userLastNames: String,
        userRut: String,
        cardNumber: String,
        cvv: String,
        method: String,
        fullAddress: String?,
        cartJson: String,
        totalAmount: Double
    ) {
        viewModelScope.launch {
            try {
                repo.savePurchase(
                    userId = userId,
                    userName = userName,
                    userLastNames = userLastNames,
                    rut = userRut,
                    cardNumber = cardNumber,
                    cvv = cvv,
                    deliveryMethod = method,
                    address = fullAddress,
                    cartJson = cartJson,
                    amount = totalAmount
                )

                _message.value = "Compra registrada exitosamente"

                // Refrescar historial
                loadPurchaseHistory()

            } catch (e: Exception) {
                _message.value = "Error al guardar la compra"
            }
        }
    }


    // ------------------------------------------------------------
    // 🔥 Cargar compras solo del usuario logueado
    // ------------------------------------------------------------
    fun loadPurchasesByUserId(userId: Int) {
        viewModelScope.launch {
            try {
                _purchaseHistory.value = repo.getPurchasesByUserId(userId)
            } catch (_: Exception) {
                _message.value = "Error al cargar compras del usuario"
            }
        }
    }


    // ------------------------------------------------------------
    // 🔥 Limpiar mensajes toast
    // ------------------------------------------------------------
    fun clearMessage() {
        _message.value = null
    }
}

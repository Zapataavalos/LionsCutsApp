package com.example.applionscuts.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.model.Appointment
import com.example.applionscuts.model.PaymentMethod
import com.example.applionscuts.model.UserProfile

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> = _appointments

    private val _paymentMethods = MutableLiveData<List<PaymentMethod>>()
    val paymentMethods: LiveData<List<PaymentMethod>> = _paymentMethods

    private val _showRedeemDialog = MutableLiveData<Boolean>(false)
    val showRedeemDialog: LiveData<Boolean> = _showRedeemDialog

    // Estado para la imagen de perfil
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private var userEmail: String = "lions@email.com"

    init {
        loadUserProfile()
    }

    fun setUserEmail(email: String) {
        this.userEmail = email
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _userProfile.value = UserProfile("uid-123", "LionsCuts", userEmail, 10)
        _appointments.value = listOf(
            Appointment("c1", "Barbero Juan", "Mid Fade", "20/10/2025", "10:00 AM"),
            Appointment("c2", "Barbero Pedro", "Corte Clásico", "15/09/2025", "03:00 PM"),
            Appointment("c3", "Barbero Luis", "Buzz Cut", "01/09/2025", "12:00 PM")
        )
        _paymentMethods.value = listOf(
            PaymentMethod("pm1", "1234", "Visa"),
            PaymentMethod("pm2", "5678", "Mastercard")
        )
    }

    fun onRedeemClicked() {
        if (_userProfile.value?.fidelityStars == 10) {
            _showRedeemDialog.value = true
        }
    }

    fun onDialogDismiss() {
        _showRedeemDialog.value = false
    }

    fun onRedeemConfirmed(rewardType: String) {
        _showRedeemDialog.value = false
        _userProfile.value = _userProfile.value?.copy(fidelityStars = 0)
    }

    // Metodo para actualizar la imagen de perfil
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }
}
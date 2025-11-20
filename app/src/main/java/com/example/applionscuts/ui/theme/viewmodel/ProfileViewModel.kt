package com.example.applionscuts.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.model.Appointment
import com.example.applionscuts.model.UserProfile

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> = _appointments

    private val _showRedeemDialog = MutableLiveData<Boolean>(false)
    val showRedeemDialog: LiveData<Boolean> = _showRedeemDialog

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _showChangePasswordDialog = MutableLiveData<Boolean>(false)
    val showChangePasswordDialog: LiveData<Boolean> = _showChangePasswordDialog

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

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun onShowChangePasswordDialog() {
        _showChangePasswordDialog.value = true
    }

    fun onHideChangePasswordDialog() {
        _showChangePasswordDialog.value = false
    }

    fun changePassword(currentPass: String, newPass: String, confirmPass: String) {
        println("Cambiando contraseña para el usuario: ${_userProfile.value?.email}")
        _showChangePasswordDialog.value = false
    }
}
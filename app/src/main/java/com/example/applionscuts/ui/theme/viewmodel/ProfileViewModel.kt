package com.example.applionscuts.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.model.UserProfile

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _appointments = MutableLiveData<List<AppointmentEntity>>()
    val appointments: LiveData<List<AppointmentEntity>> = _appointments

    private val _showRedeemDialog = MutableLiveData<Boolean>(false)
    val showRedeemDialog: LiveData<Boolean> = _showRedeemDialog

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _showChangePasswordDialog = MutableLiveData<Boolean>(false)
    val showChangePasswordDialog: LiveData<Boolean> = _showChangePasswordDialog

    private var userEmail: String = "lions@gmail.com"

    init {
        loadUserProfile()
    }

    fun setUserEmail(email: String) {
        this.userEmail = email
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _userProfile.value = UserProfile("uid-123", "LionsCuts", userEmail, 10)

        // YA NO cargamos citas fijas aquí
        _appointments.value = emptyList()
    }

    // NUEVO —— permite a ProfileScreen actualizar las citas reales del usuario
    fun setAppointments(list: List<AppointmentEntity>) {
        _appointments.value = list
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

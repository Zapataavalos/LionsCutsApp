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

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _showChangePasswordDialog = MutableLiveData(false)
    val showChangePasswordDialog: LiveData<Boolean> = _showChangePasswordDialog

    private val _showRedeemDialog = MutableLiveData(false)
    val showRedeemDialog: LiveData<Boolean> = _showRedeemDialog


    fun updateUserName(newName: String) {
        _userProfile.value = _userProfile.value?.copy(name = newName)
    }

    fun updateUserPhone(newPhone: String) {
        _userProfile.value = _userProfile.value?.copy(phone = newPhone)
    }


    // -----------------------------------------------------
    // Recibir datos del AuthViewModel
    // -----------------------------------------------------
    fun updateUserFromAuth(id: String, name: String, email: String, phone: String) {
        _userProfile.value = UserProfile(
            uid = id,
            name = name,
            email = email,
            phone = phone,
            fidelityStars = 10  // lleno
        )
    }

    // -----------------------------------------------------
    fun updateUserName(newName: String, authViewModel: AuthViewModel) {
        _userProfile.value = _userProfile.value?.copy(name = newName)
        authViewModel.updateCurrentUser(name = newName)
    }

    fun updateUserPhone(newPhone: String, authViewModel: AuthViewModel) {
        _userProfile.value = _userProfile.value?.copy(phone = newPhone)
        authViewModel.updateCurrentUser(phone = newPhone)
    }

    // -----------------------------------------------------
    fun setAppointments(list: List<AppointmentEntity>) {
        _appointments.value = list
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    // -----------------------------------------------------
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

    // -----------------------------------------------------
    fun onShowChangePasswordDialog() {
        _showChangePasswordDialog.value = true
    }

    fun onHideChangePasswordDialog() {
        _showChangePasswordDialog.value = false
    }

    fun changePassword(currentPass: String, newPass: String, confirmPass: String) {
        println("Password updated for ${_userProfile.value?.email}")
        _showChangePasswordDialog.value = false
    }


}

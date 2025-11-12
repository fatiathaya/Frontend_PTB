package com.example.projektbptb.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projektbptb.R
import com.example.projektbptb.model.User

class SettingsViewModel : ViewModel() {

    val user = mutableStateOf(
        User(
            name = "Jiharmok",
            email = "jiharmokgaming@gmail.com",
            profileImageRes = R.drawable.profil // ganti sesuai aset
        )
    )

    val isNotificationEnabled = mutableStateOf(true)

    fun toggleNotification() {
        isNotificationEnabled.value = !isNotificationEnabled.value
    }
}

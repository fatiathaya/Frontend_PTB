package com.example.projektbptb.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.projektbptb.data.model.Address

class AddressViewModel : ViewModel() {
    
    val savedAddresses = mutableStateListOf(
        Address(
            id = "1",
            label = "Rumah",
            fullAddress = "MAC 2024, Kec. Pauh, Kota Padang, Sumatera Barat, Indonesia",
            locationName = "Kost Bang Am",
            detailLocation = "",
            landmark = ""
        )
    )

    fun addAddress(address: Address) {
        savedAddresses.add(address.copy(id = System.currentTimeMillis().toString()))
    }

    fun updateAddress(oldAddress: Address, newAddress: Address) {
        val index = savedAddresses.indexOf(oldAddress)
        if (index != -1) {
            savedAddresses[index] = newAddress.copy(id = oldAddress.id)
        }
    }

    fun deleteAddress(address: Address) {
        savedAddresses.remove(address)
    }
}


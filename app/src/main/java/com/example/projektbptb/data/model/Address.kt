package com.example.projektbptb.data.model

data class Address(
    val id: String = "",
    val label: String = "", // "Rumah", "Kantor", etc.
    val fullAddress: String = "",
    val locationName: String = "", // "Kost Bang Am"
    val detailLocation: String = "", // Optional: "Nama bangunan/no.unit/lantai"
    val landmark: String = "" // Optional: "Sebelah toko grosir"
)


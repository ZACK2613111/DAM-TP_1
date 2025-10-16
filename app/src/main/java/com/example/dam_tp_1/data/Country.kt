package com.example.dam_tp_1.data

data class Country(
    val name: CountryName,
    val cca2: String,
    val flags: Flags
)

data class CountryName(
    val common: String,
    val official: String
)

data class Flags(
    val png: String,
    val svg: String
)

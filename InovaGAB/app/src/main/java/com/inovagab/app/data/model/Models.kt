package com.inovagab.app.data.model

enum class UserProfile {
    OPERADOR, GESTOR, LIDER
}

data class User(
    val id: String,
    val nome: String,
    val email: String,
    val senha: String,
    val perfil: UserProfile
)

data class StrategicGuideline(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val ativo: Boolean = true
)

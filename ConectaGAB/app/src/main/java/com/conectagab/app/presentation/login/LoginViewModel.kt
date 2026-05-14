package com.conectagab.app.presentation.login

import androidx.lifecycle.ViewModel
import com.conectagab.app.data.model.User
import com.conectagab.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(private val repository: AppRepository) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun login(onSuccess: (User) -> Unit) {
        val result = repository.login(email.value, password.value)
        result.onSuccess { user ->
            _loginError.value = null
            onSuccess(user)
        }.onFailure {
            _loginError.value = "Email ou senha incorretos."
        }
    }
}

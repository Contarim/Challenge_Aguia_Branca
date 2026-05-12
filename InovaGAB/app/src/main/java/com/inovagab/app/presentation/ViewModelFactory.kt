package com.inovagab.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inovagab.app.AppContainer
import com.inovagab.app.presentation.login.LoginViewModel
import com.inovagab.app.presentation.home.OperatorViewModel
import com.inovagab.app.presentation.home.ManagerViewModel
import com.inovagab.app.presentation.home.LeaderViewModel

object AppViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(AppContainer.repository) as T
            }
            modelClass.isAssignableFrom(OperatorViewModel::class.java) -> {
                OperatorViewModel(AppContainer.repository) as T
            }
            modelClass.isAssignableFrom(ManagerViewModel::class.java) -> {
                ManagerViewModel(AppContainer.repository) as T
            }
            modelClass.isAssignableFrom(LeaderViewModel::class.java) -> {
                LeaderViewModel(AppContainer.repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

/*
 * Copyright 2020 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.R
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.application.AuthenticationManager
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.core.navigation.NavigationCommand
import toothpick.InjectConstructor

@InjectConstructor
class LoginViewModel(
    private val authenticationManager: AuthenticationManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val errorEmitter: EventEmitter<String> = EventEmitter()
    val errorEvents: EventSource<String> get() = errorEmitter

    private val navigationEmitter: EventEmitter<NavigationCommand> = EventEmitter()
    val navigationCommands: EventSource<NavigationCommand> get() = navigationEmitter

    val username: MutableLiveData<String> = savedStateHandle.getLiveData("username", "")
    val password: MutableLiveData<String> = savedStateHandle.getLiveData("password", "")

    fun onLoginClicked() {
        if (username.value!!.isNotBlank() && password.value!!.isNotBlank()) {
            authenticationManager.saveRegistration()

            navigationEmitter.emit { navController, context ->
                navController.navigate(R.id.logged_out_to_logged_in)
            }
        } else {
            errorEmitter.emit("Invalid username or password!")
        }
    }

    fun onRegisterClicked() {
        navigationEmitter.emit { navController, context ->
            navController.navigate(R.id.logged_out_to_registration)
        }
    }
}
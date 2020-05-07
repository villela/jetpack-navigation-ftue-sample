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
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.features.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.R
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.application.AuthenticationManager
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.core.navigation.NavigationCommand
import com.zhuinden.livedatacombinetuplekt.combineTuple
import toothpick.InjectConstructor

@InjectConstructor
class RegistrationViewModel(
    private val authenticationManager: AuthenticationManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navigationEmitter: EventEmitter<NavigationCommand> = EventEmitter()
    val navigationCommands: EventSource<NavigationCommand> get() = navigationEmitter

    enum class RegistrationState { // this is actually kinda superfluous/unnecessary but ok
        COLLECT_PROFILE_DATA,
        COLLECT_USER_PASSWORD,
        REGISTRATION_COMPLETED
    }

    private var currentState: MutableLiveData<RegistrationState> =
        savedStateHandle.getLiveData("currentState", RegistrationState.COLLECT_PROFILE_DATA)

    val fullName: MutableLiveData<String> = savedStateHandle.getLiveData("fullName", "")
    val bio: MutableLiveData<String> = savedStateHandle.getLiveData("bio", "")

    val isEnterProfileNextEnabled = combineTuple(fullName, bio).map { (fullName, bio) ->
        fullName!!.isNotBlank() && bio!!.isNotBlank()
    }

    val username: MutableLiveData<String> = savedStateHandle.getLiveData("username", "")
    val password: MutableLiveData<String> = savedStateHandle.getLiveData("password", "")

    val isRegisterAndLoginEnabled = combineTuple(username, password).map { (username, password) ->
        username!!.isNotBlank() && password!!.isNotBlank()
    }

    fun onEnterProfileNextClicked() {
        if (fullName.value!!.isNotBlank() && bio.value!!.isNotBlank()) {
            currentState.value = RegistrationState.COLLECT_USER_PASSWORD
            navigationEmitter.emit { navController, context ->
                navController.navigate(R.id.enter_profile_data_to_create_login_credentials)
            }
        }
    }

    fun onRegisterAndLoginClicked() {
        if (username.value!!.isNotBlank() && password.value!!.isNotBlank()) {
            currentState.value = RegistrationState.REGISTRATION_COMPLETED
            authenticationManager.saveRegistration()
            navigationEmitter.emit { navController, context ->
                navController.navigate(R.id.registration_to_logged_in)
            }
        }
    }

    fun onCreateLoginCredentialsBackEvent() {
        currentState.value = RegistrationState.COLLECT_USER_PASSWORD
        navigationEmitter.emit { navController, context ->
            navController.popBackStack()
        }
    }
}

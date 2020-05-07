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
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.ktp.KTP
import java.util.*

fun SavedStateRegistryOwner.createAbstractSavedStateViewModelFactory(
    arguments: Bundle
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(this, arguments) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            val scope = KTP.openRootScope()
                .openSubScope(UUID.randomUUID().toString())
                .installModules(object : Module() {
                    init {
                        bind(Bundle::class.java).toInstance(arguments)
                        bind(SavedStateHandle::class.java).toInstance(handle)
                    }
                })
            val viewModel = scope.getInstance(modelClass)
            KTP.closeScope(scope)
            return viewModel
        }
    }
}

inline fun <reified T : ViewModel> Fragment.navGraphSavedStateViewModels(
    @IdRes navGraphId: Int
): Lazy<T> {
    // Wrapped in lazy to not search the NavController each time we want the backStackEntry
    val backStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }

    return createViewModelLazy(T::class, storeProducer = {
        backStackEntry.viewModelStore
    }, factoryProducer = {
        backStackEntry.createAbstractSavedStateViewModelFactory(arguments ?: Bundle())
    })
}

inline fun <reified T : ViewModel> Fragment.fragmentViewModels(): Lazy<T> {
    return createViewModelLazy(T::class, storeProducer = {
        viewModelStore
    }, factoryProducer = {
        createAbstractSavedStateViewModelFactory(arguments ?: Bundle())
    })
}

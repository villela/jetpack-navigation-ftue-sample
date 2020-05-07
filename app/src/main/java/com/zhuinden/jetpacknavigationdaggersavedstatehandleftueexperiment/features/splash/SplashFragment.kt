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
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.features.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.R
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.application.AuthenticationManager
import toothpick.Toothpick

class SplashFragment : Fragment(R.layout.splash_fragment) {
    private val handler = Handler()

    private val finishSplash: Runnable = Runnable {
        val authenticationManager =
            Toothpick.openRootScope().getInstance(AuthenticationManager::class.java)

        if (authenticationManager.isAuthenticated()) {
            Navigation.findNavController(requireView()).navigate(R.id.splash_to_logged_in)
        } else {
            Navigation.findNavController(requireView()).navigate(R.id.splash_to_logged_out)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed(finishSplash, 1L)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(finishSplash)
        super.onDestroyView()
    }
}
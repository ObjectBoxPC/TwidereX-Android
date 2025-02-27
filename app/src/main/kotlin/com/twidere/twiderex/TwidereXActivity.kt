/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.foundation.LocalInAppNotification
import com.twidere.twiderex.di.assisted.ProvideAssistedFactory
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalActivity
import com.twidere.twiderex.ui.LocalApplication
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalWindow
import com.twidere.twiderex.ui.LocalWindowInsetsController
import com.twidere.twiderex.utils.CustomTabSignInChannel
import com.twidere.twiderex.utils.IsActiveNetworkMeteredLiveData
import com.twidere.twiderex.utils.LocalPlatformResolver
import com.twidere.twiderex.utils.PlatformResolver
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.precompose.navigation.NavController
import javax.inject.Inject

@AndroidEntryPoint
class TwidereXActivity : ComponentActivity() {

    private val navController by lazy {
        NavController()
    }

    @Inject
    lateinit var viewModelHolder: TwidereXActivityAssistedViewModelHolder

    @Inject
    lateinit var statusActions: StatusActions

    @Inject
    lateinit var preferencesHolder: PreferencesHolder

    @Inject
    lateinit var inAppNotification: InAppNotification

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    lateinit var platformResolver: PlatformResolver

    private val isActiveNetworkMetered = MutableStateFlow(false)
    private val isActiveNetworkMeteredLiveData by lazy {
        IsActiveNetworkMeteredLiveData(connectivityManager = connectivityManager)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        isActiveNetworkMeteredLiveData.observe(this) {
            isActiveNetworkMetered.value = it
        }
        setContent {
            var showSplash by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                preferencesHolder.warmup()
                showSplash = false
            }
            App()
            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Splash()
            }
        }
        intent.data?.let {
            onDeeplink(it)
        }
    }

    @Composable
    private fun Splash() {
        MaterialTheme(
            colors = if (isSystemInDarkTheme()) {
                darkColors()
            } else {
                lightColors()
            }
        ) {
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_logo),
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_twidere)
                    )
                }
            }
        }
    }

    @Composable
    private fun App() {
        val windowInsetsControllerCompat =
            remember { WindowInsetsControllerCompat(window, window.decorView) }
        val accountViewModel = viewModel<ActiveAccountViewModel>()
        val account by accountViewModel.account.observeAsState(null)
        val isActiveNetworkMetered by isActiveNetworkMetered.observeAsState(initial = false)
        CompositionLocalProvider(
            LocalInAppNotification provides inAppNotification,
            LocalWindow provides window,
            LocalWindowInsetsController provides windowInsetsControllerCompat,
            LocalActiveAccount provides account,
            LocalApplication provides application,
            LocalStatusActions provides statusActions,
            LocalActivity provides this,
            LocalActiveAccountViewModel provides accountViewModel,
            LocalIsActiveNetworkMetered provides isActiveNetworkMetered,
            LocalPlatformResolver provides platformResolver,
        ) {
            ProvidePreferences(
                preferencesHolder,
            ) {
                ProvideAssistedFactory(
                    viewModelHolder.factory,
                ) {
                    ProvideWindowInsets(
                        windowInsetsAnimationsEnabled = true
                    ) {
                        Router(
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun onDeeplink(it: Uri) {
        if (CustomTabSignInChannel.canHandle(it)) {
            lifecycleScope.launchWhenResumed {
                CustomTabSignInChannel.send(it)
            }
        } else {
            navController.navigate(it.toString())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.data?.let {
            onDeeplink(it)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            CustomTabSignInChannel.onClose()
        }
    }
}

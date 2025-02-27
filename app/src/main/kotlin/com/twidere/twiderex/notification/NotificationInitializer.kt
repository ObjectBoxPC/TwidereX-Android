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
package com.twidere.twiderex.notification

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.twidere.twiderex.di.InitializerEntryPoint
import com.twidere.twiderex.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationInitializerHolder

private const val NotificationWorkName = "twiderex_notification"

class NotificationInitializer : Initializer<NotificationInitializerHolder> {
    @Inject
    lateinit var workManager: WorkManager

    override fun create(context: Context): NotificationInitializerHolder {
        InitializerEntryPoint.resolve(context).inject(this)
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            NotificationWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        return NotificationInitializerHolder()
    }

    override fun dependencies() = listOf(
        NotificationChannelInitializer::class.java,
    )
}

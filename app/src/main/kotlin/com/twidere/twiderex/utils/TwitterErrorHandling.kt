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
package com.twidere.twiderex.utils

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import com.twidere.services.http.MicroBlogHttpException
import com.twidere.services.mastodon.model.exceptions.MastodonException
import com.twidere.services.twitter.TwitterErrorCodes
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.services.utils.MicroBlogJsonException
import com.twidere.twiderex.R
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.notification.NotificationEvent
import com.twidere.twiderex.notification.StringNotificationEvent
import com.twidere.twiderex.notification.StringResNotificationEvent
import com.twidere.twiderex.notification.StringResWithActionNotificationEvent
import java.util.concurrent.CancellationException

fun Throwable.generateNotificationEvent(): NotificationEvent? {
    return when (this) {
        is MicroBlogHttpException -> {
            when (this.httpCode) {
                HttpErrorCodes.TooManyRequests -> {
                    return StringResNotificationEvent(messageId = R.string.common_alerts_too_many_requests_title)
                }
                else -> null
            }
        }
        is MicroBlogJsonException -> {
            microBlogErrorMessage?.let { StringNotificationEvent(it) }
        }
        is TwitterApiException -> {
            when (this.errors?.firstOrNull()?.code) {
                TwitterErrorCodes.TemporarilyLocked -> {
                    StringResWithActionNotificationEvent(
                        R.string.common_alerts_account_temporarily_locked_title,
                        R.string.common_alerts_account_temporarily_locked_message,
                        actionId = R.string.common_controls_actions_ok
                    ) {
                        context.startActivity(
                            Intent(
                                ACTION_VIEW,
                                Uri.parse("https://twitter.com/login")
                            )
                        )
                    }
                }
                TwitterErrorCodes.RateLimitExceeded -> null
                else -> microBlogErrorMessage?.let { StringNotificationEvent(it) }
            }
        }
        is TwitterApiExceptionV2 -> {
            detail?.let { StringNotificationEvent(it) }
        }
        is MastodonException -> {
            microBlogErrorMessage?.let { StringNotificationEvent(it) }
        }
        !is CancellationException -> {
            message?.let { StringNotificationEvent(it) }
        }
        else -> null
    }
}

fun Throwable.notify(notification: InAppNotification) {
    generateNotificationEvent()?.let {
        notification.show(it)
    } ?: throw this
}

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
package com.twidere.twiderex.jobs.dm

import android.content.Context
import com.twidere.services.microblog.LookupService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.autolink
import com.twidere.twiderex.db.mapper.toDbDirectMessage
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.job.DirectMessageSendData
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.repository.AccountRepository

class TwitterDirectMessageSendJob(
    context: Context,
    accountRepository: AccountRepository,
    notificationManager: AppNotificationManager,
    fileResolver: FileResolver,
    cacheDatabase: CacheDatabase,
) : DirectMessageSendJob<TwitterService>(
    context, cacheDatabase, accountRepository, notificationManager, fileResolver
) {

    override suspend fun sendMessage(
        service: TwitterService,
        sendData: DirectMessageSendData,
        mediaIds: ArrayList<String>
    ): DbDMEventWithAttachments = service.sendDirectMessage(
        recipientId = sendData.recipientUserKey.id,
        text = sendData.text,
        attachmentType = "media",
        mediaId = mediaIds.firstOrNull()
    )?.toDbDirectMessage(
        accountKey = sendData.accountKey,
        sender = lookUpUser(cacheDatabase, sendData.accountKey, service)
    ) ?: throw Error()

    private suspend fun lookUpUser(database: CacheDatabase, userKey: MicroBlogKey, service: TwitterService): DbUser {
        return database.userDao().findWithUserKey(userKey) ?: let {
            val user = (service as LookupService).lookupUser(userKey.id)
                .toDbUser(userKey)
            database.userDao().insertAll(listOf(user))
            user
        }
    }

    override suspend fun uploadImage(
        originUri: String,
        scramblerUri: String,
        service: TwitterService
    ): String? {
        val type = fileResolver.getMimeType(originUri)
        val size = fileResolver.getFileSize(originUri)
        return fileResolver.openInputStream(scramblerUri)?.use {
            service.uploadFile(
                it,
                type ?: "image/*",
                size ?: it.available().toLong()
            )
        } ?: throw Error()
    }

    override suspend fun autoLink(text: String): String {
        return autolink.autoLink(text)
    }
}

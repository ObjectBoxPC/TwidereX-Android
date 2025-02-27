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
package com.twidere.twiderex.paging.mediator.paging

import androidx.paging.PagingState
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.ArrayListCompat
import com.twidere.twiderex.paging.IPagination

class CursorWithCustomOrderPagingResult<T>(
    data: List<T>,
    val cursor: String? = null,
    val nextOrder: Long = 0,
) : ArrayListCompat<T>(data)

data class CursorWithCustomOrderPagination(
    val cursor: String?,
    val nextOrder: Long,
) : IPagination

abstract class CursorWithCustomOrderPagingMediator(
    accountKey: MicroBlogKey,
    database: CacheDatabase,
) : PagingTimelineMediatorBase<CursorWithCustomOrderPagination>(
    accountKey,
    database
) {
    override fun provideNextPage(
        raw: List<IStatus>,
        result: List<DbPagingTimelineWithStatus>
    ): CursorWithCustomOrderPagination {
        return if (raw is CursorWithCustomOrderPagingResult<*>) {
            CursorWithCustomOrderPagination(
                cursor = raw.cursor,
                nextOrder = raw.nextOrder - result.size
            )
        } else {
            CursorWithCustomOrderPagination(
                cursor = result.lastOrNull()?.status?.status?.data?.statusId,
                nextOrder = (result.lastOrNull()?.timeline?.sortId ?: 0) - result.size
            )
        }
    }

    override fun transform(
        state: PagingState<Int, DbPagingTimelineWithStatus>,
        data: List<DbPagingTimelineWithStatus>,
        list: List<IStatus>
    ): List<DbPagingTimelineWithStatus> {
        val lastId = if (list is CursorWithCustomOrderPagingResult<*>) {
            list.nextOrder
        } else {
            state.lastItemOrNull()?.timeline?.sortId ?: 0
        }
        return data.mapIndexed { index, dbPagingTimelineWithStatus ->
            dbPagingTimelineWithStatus.copy(
                timeline = dbPagingTimelineWithStatus.timeline.copy(
                    sortId = lastId - index
                )
            )
        }
    }
}

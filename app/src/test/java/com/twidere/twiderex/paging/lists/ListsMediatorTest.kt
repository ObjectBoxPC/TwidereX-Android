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
package com.twidere.twiderex.paging.lists

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.db.model.DbList
import com.twidere.twiderex.mock.MockCenter
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.list.ListsMediator
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * instead of testing pagination, we should focus on our code logic
 */

@RunWith(MockitoJUnitRunner::class)
class ListsMediatorTest {
    private var mockDataBase = MockCenter.mockCacheDatabase()

    private var mockService = MockCenter.mockListsService() as ListsService

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_saveToDatabaseWhenSuccess() {
        runBlocking {
            Assert.assertEquals(0, mockDataBase.listsDao().findAll()?.size)
            val mediator = ListsMediator(mockDataBase, mockService, accountKey = MicroBlogKey.twitter("123"))
            val pagingState = PagingState<Int, DbList>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
            mediator.load(LoadType.REFRESH, pagingState)
            // when mediator get data from service, it store to database
            assert(mockDataBase.listsDao().findAll()?.isNotEmpty() == true)
        }
    }
}

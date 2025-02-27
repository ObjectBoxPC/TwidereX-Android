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
package com.twidere.twiderex.mock.db

import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.Flow

class MockUserDao : UserDao {
    override suspend fun insertAll(user: List<DbUser>) {
    }

    override fun findWithUserKeyFlow(userKey: MicroBlogKey): Flow<DbUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun findWithUserKey(userKey: MicroBlogKey): DbUser? {
        TODO("Not yet implemented")
    }

    override suspend fun update(user: List<DbUser>) {
        TODO("Not yet implemented")
    }
}

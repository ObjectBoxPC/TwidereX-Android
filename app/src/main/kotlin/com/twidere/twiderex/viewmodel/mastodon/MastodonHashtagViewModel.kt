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
package com.twidere.twiderex.viewmodel.mastodon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.TimelineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class MastodonHashtagViewModel @AssistedInject constructor(
    private val repository: TimelineRepository,
    @Assisted keyword: String,
    @Assisted account: AccountDetails,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            keyword: String,
            account: AccountDetails,
        ): MastodonHashtagViewModel
    }

    val source by lazy {
        repository.mastodonHashtagTimeline(
            keyword = keyword,
            account = account,
        ).cachedIn(viewModelScope)
    }
}

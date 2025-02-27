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
package com.twidere.twiderex.scenes.search.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.viewmodel.search.SearchTweetsViewModel

class SearchTweetsItem : SearchSceneItem {
    @Composable
    override fun name(): String {
        return stringResource(id = R.string.scene_search_tabs_tweets)
    }

    @Composable
    override fun Content(keyword: String) {
        val account = LocalActiveAccount.current ?: return
        val viewModel =
            assistedViewModel<SearchTweetsViewModel.AssistedFactory, SearchTweetsViewModel> {
                it.create(account, keyword)
            }
        val source = viewModel.source.collectAsLazyPagingItems()
        SwipeToRefreshLayout(
            refreshingState = source.loadState.refresh is LoadState.Loading,
            onRefresh = {
                source.refreshOrRetry()
            }
        ) {
            LazyUiStatusList(
                items = source,
            )
        }
    }
}

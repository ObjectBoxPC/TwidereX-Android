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
package com.twidere.twiderex.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.repository.CacheRepository
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class StorageViewModel @AssistedInject constructor(
    private val repository: CacheRepository,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): StorageViewModel
    }

    val loading = MutableStateFlow(false)

    fun clearDatabaseCache() = viewModelScope.launch {
        loading.value = true
        repository.clearDatabaseCache()
        loading.value = false
    }

    fun clearImageCache() = viewModelScope.launch {
        loading.value = true
        repository.clearImageCache()
        repository.clearCacheDir()
        loading.value = false
    }

    fun clearSearchHistory() = viewModelScope.launch {
        loading.value = true
        repository.clearSearchHistory()
        loading.value = false
    }
}

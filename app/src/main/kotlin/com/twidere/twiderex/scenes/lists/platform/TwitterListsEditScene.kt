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
package com.twidere.twiderex.scenes.lists.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lists.TwitterListsModifyComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsModifyViewModel

@Composable
fun TwitterListsEditScene(
    listKey: MicroBlogKey
) {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val listsEditViewModel = assistedViewModel<ListsModifyViewModel.AssistedFactory, ListsModifyViewModel>(
        account, listKey
    ) {
        it.create(account, listKey)
    }
    val loading by listsEditViewModel.loading.observeAsState(initial = false)
    val source by listsEditViewModel.source.observeAsState(null)
    source?.let { uiList ->
        TwidereScene {
            val name by listsEditViewModel.editName.observeAsState(uiList.title)
            val desc by listsEditViewModel.editDesc.observeAsState(uiList.descriptions)
            val isPrivate by listsEditViewModel.editPrivate.observeAsState(uiList.isPrivate)
            InAppNotificationScaffold(
                topBar = {
                    AppBar(
                        navigationIcon = { AppBarNavigationButton(Icons.Default.Close) },
                        title = {
                            Text(text = stringResource(id = R.string.scene_lists_modify_edit_title))
                        },
                        actions = {
                            IconButton(
                                enabled = name.isNotEmpty(),
                                onClick = {
                                    listsEditViewModel.editList(
                                        listKey.id,
                                        title = name,
                                        description = desc,
                                        private = isPrivate
                                    ) { success, _ ->
                                        if (success) navController.popBackStack()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.common_controls_actions_confirm),
                                    tint = if (name.isNotEmpty()) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                                )
                            }
                        }
                    )
                }
            ) {
                Box {
                    TwitterListsModifyComponent(
                        name = name,
                        desc = desc,
                        isPrivate = isPrivate,
                        onNameChanged = { listsEditViewModel.editName.value = it },
                        onDescChanged = { listsEditViewModel.editDesc.value = it },
                        onPrivateChanged = { listsEditViewModel.editPrivate.value = it }
                    )
                    if (loading) {
                        Dialog(onDismissRequest = { }) {
                            LoadingProgress()
                        }
                    }
                }
            }
        }
    }
}

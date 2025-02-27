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
package com.twidere.twiderex.scenes.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.scenes.lists.platform.MastodonListsEditDialog
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsModifyViewModel
import com.twidere.twiderex.viewmodel.lists.ListsTimelineViewModel

@Composable
fun ListTimeLineScene(
    listKey: MicroBlogKey
) {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val viewModel = assistedViewModel<ListsModifyViewModel.AssistedFactory, ListsModifyViewModel>(
        account, listKey
    ) {
        it.create(account, listKey)
    }
    val source by viewModel.source.observeAsState(initial = null)
    val loading by viewModel.loading.observeAsState(initial = false)
    var showEditDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = source?.title ?: stringResource(id = R.string.scene_lists_details_title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (source?.isPrivate == true)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = stringResource(id = R.string.scene_lists_icons_private),
                                    modifier = Modifier
                                        .alpha(ContentAlpha.disabled)
                                        .padding(start = ListTimelineSceneDefaults.LockIconPadding)
                                        .size(ListTimelineSceneDefaults.LockIconSize)
                                )
                        }
                    },
                    actions = {
                        var menuExpand by remember {
                            mutableStateOf(false)
                        }
                        IconButton(onClick = { menuExpand = !menuExpand }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(
                                    id = R.string.scene_lists_details_menu_actions_edit_list
                                )
                            )
                        }
                        source?.let { uiList ->
                            DropdownMenu(
                                expanded = menuExpand,
                                onDismissRequest = { menuExpand = false }
                            ) {
                                if (!uiList.isOwner(account.user.userId)) {
                                    val following = uiList.isFollowed
                                    DropdownMenuItem(
                                        onClick = {
                                            if (following) {
                                                viewModel.unsubscribeList(uiList.listKey)
                                            } else {
                                                viewModel.subscribeList(uiList.listKey)
                                            }
                                            menuExpand = false
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(
                                                id = if (following)
                                                    R.string.scene_lists_details_menu_actions_unfollow
                                                else
                                                    R.string.scene_lists_details_menu_actions_follow
                                            )
                                        )
                                    }
                                }

                                DropdownMenuItem(
                                    onClick = {
                                        menuExpand = false
                                        navController.navigate(
                                            RootRoute.Lists.Members(
                                                listKey,
                                                uiList.isOwner(account.user.userId)
                                            )
                                        )
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.scene_lists_details_tabs_members))
                                }

                                if (uiList.allowToSubscribe) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menuExpand = false
                                            navController.navigate(
                                                RootRoute.Lists.Subscribers(
                                                    listKey
                                                )
                                            )
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.scene_lists_details_tabs_subscriber))
                                    }
                                }

                                if (uiList.isOwner(account.user.userId)) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menuExpand = false
                                            when (account.type) {
                                                PlatformType.Twitter -> navController.navigate(RootRoute.Lists.TwitterEdit(listKey = listKey))
                                                PlatformType.StatusNet -> TODO()
                                                PlatformType.Fanfou -> TODO()
                                                PlatformType.Mastodon -> showEditDialog = true
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (account.type == PlatformType.Mastodon)
                                                stringResource(id = R.string.scene_lists_details_menu_actions_rename_list)
                                            else
                                                stringResource(id = R.string.scene_lists_details_menu_actions_edit_list)
                                        )
                                    }
                                }

                                if (uiList.isOwner(account.user.userId)) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menuExpand = false
                                            showDeleteConfirmDialog = true
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.scene_lists_details_menu_actions_delete_list))
                                    }
                                }
                            }
                        }
                    },
                )
            },
        ) {
            Box {
                ListTimelineComponent(account, listKey)
                if (showEditDialog) {
                    MastodonListsEditDialog(listKey) {
                        showEditDialog = false
                    }
                }
                if (loading) {
                    Dialog(onDismissRequest = { }) {
                        LoadingProgress()
                    }
                }
                source?.let {
                    if (showDeleteConfirmDialog) {
                        ListDeleteConfirmDialog(
                            title = it.title,
                            onDismissRequest = {
                                showDeleteConfirmDialog = false
                            }
                        ) {
                            viewModel.deleteList { success, _ ->
                                if (success) navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListTimelineComponent(account: AccountDetails, listKey: MicroBlogKey) {
    val viewModel = assistedViewModel<ListsTimelineViewModel.AssistedFactory, ListsTimelineViewModel>(
        account, listKey
    ) {
        it.create(account, listKey)
    }
    val timelineSource = viewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    SwipeToRefreshLayout(
        refreshingState = timelineSource.loadState.refresh is LoadState.Loading,
        onRefresh = { timelineSource.refresh() }
    ) {
        LazyUiStatusList(
            items = timelineSource,
        )
    }
}

@Composable
private fun ListDeleteConfirmDialog(title: String, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = {
            Text(
                text = stringResource(id = R.string.scene_lists_details_delete_list_title, title),
                style = MaterialTheme.typography.subtitle1
            )
        },
        text = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = title, style = MaterialTheme.typography.body2)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.common_controls_actions_yes))
            }
        },

    )
}

private object ListTimelineSceneDefaults {
    val LockIconSize = 24.dp
    val LockIconPadding = 8.dp
}

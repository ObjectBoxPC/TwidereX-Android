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
package com.twidere.twiderex.model.transform

import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.model.ui.UiMedia

fun List<DbMedia>.toUi() = sortedBy { it.order }.map {
    UiMedia(
        url = it.url,
        mediaUrl = it.mediaUrl,
        previewUrl = it.previewUrl,
        type = it.type,
        width = it.width,
        height = it.height,
        pageUrl = it.pageUrl,
        altText = it.altText,
    )
}

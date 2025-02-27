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
package com.twidere.twiderex.db.mapper

import com.twidere.services.gif.giphy.GifObject
import com.twidere.services.gif.model.IGif
import com.twidere.twiderex.model.ui.UiGif
import java.util.UUID

typealias GiphyGif = GifObject

fun IGif.toUi(): UiGif {
    return when (this) {
        is GiphyGif -> UiGif(
            id = this.id ?: UUID.randomUUID().toString(),
            url = this.images?.original?.url ?: "",
            mp4 = this.images?.original?.mp4 ?: "",
            preview = this.images?.previewGif?.url ?: "",
            type = this.type ?: "gif"
        )
        else -> throw NotImplementedError()
    }
}

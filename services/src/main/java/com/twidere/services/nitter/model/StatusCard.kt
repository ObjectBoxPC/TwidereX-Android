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
package com.twidere.services.nitter.model

import moe.tlaster.hson.annotations.HtmlSerializable

data class StatusCard(
    @HtmlSerializable("a", attr = "href")
    val link: String? = null,
    @HtmlSerializable("img", attr = "src")
    val img: String? = null,
    @HtmlSerializable(".card-title")
    val title: String? = null,
    @HtmlSerializable(".card-description")
    val desc: String? = null,
    @HtmlSerializable(".card-destination")
    val destination: String? = null,
)

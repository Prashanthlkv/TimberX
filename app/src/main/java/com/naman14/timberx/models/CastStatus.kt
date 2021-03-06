/*
 * Copyright (c) 2019 Naman Dwivedi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package com.naman14.timberx.models

import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.naman14.timberx.util.Constants

data class CastStatus(
    var isCasting: Boolean = false,
    var castDeviceName: String = "",
    var castSongId: Int = -1,
    var castAlbumId: Int = -1,
    var castSongTitle: String = "",
    var castSongArtist: String = "",
    var state: Int = STATUS_NONE
) {

    companion object {
        const val STATUS_NONE = -1
        const val STATUS_PLAYING = 0
        const val STATUS_PAUSED = 1
        const val STATUS_BUFFERING = 2
    }

    fun fromRemoteMediaClient(deviceName: String, remoteMediaClient: RemoteMediaClient?): CastStatus {
        remoteMediaClient ?: return this.apply { isCasting = false }

        castDeviceName = deviceName

        state = if (remoteMediaClient.isBuffering) STATUS_BUFFERING
        else if (remoteMediaClient.isPlaying) STATUS_PLAYING
        else if (remoteMediaClient.isPaused) STATUS_PAUSED
        else STATUS_NONE

        remoteMediaClient.currentItem?.media?.metadata?.let {
            castSongTitle = it.getString(MediaMetadata.KEY_TITLE)
            castSongArtist = it.getString(MediaMetadata.KEY_ARTIST)
            castSongId = it.getInt(Constants.CAST_MUSIC_METADATA_ID)
            castAlbumId = it.getInt(Constants.CAST_MUSIC_METADATA_ALBUM_ID)
        }

        isCasting = true
        return this
    }
}

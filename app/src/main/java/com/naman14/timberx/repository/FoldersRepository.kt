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
package com.naman14.timberx.repository

import android.text.TextUtils
import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator

object FoldersRepository {

    private val SUPPORTED_EXT = arrayOf("mp3", "mp4", "m4a", "aac", "ogg", "wav")

    fun getMediaFiles(dir: File, acceptDirs: Boolean): List<File> {
        val list = ArrayList<File>()
        list.add(File(dir, ".."))
        if (dir.isDirectory) {
            val files = Arrays.asList(*dir.listFiles { file ->
                if (file.isFile) {
                    val name = file.name
                    ".nomedia" != name && checkFileExt(name)
                } else if (file.isDirectory) {
                    acceptDirs && checkDir(file)
                } else
                    false
            }!!)
            Collections.sort(files, FilenameComparator())
            Collections.sort(files, DirFirstComparator())
            list.addAll(files)
        }

        return list
    }

    fun isMediaFile(file: File): Boolean {
        return file.exists() && file.canRead() && checkFileExt(file.name)
    }

    private fun checkDir(dir: File): Boolean {
        return dir.exists() && dir.canRead() && "." != dir.name && dir.listFiles { pathname ->
            val name = pathname.name
            "." != name && ".." != name && pathname.canRead() && (pathname.isDirectory || pathname.isFile && checkFileExt(name))
        }!!.size != 0
    }

    private fun checkFileExt(name: String): Boolean {
        if (TextUtils.isEmpty(name)) {
            return false
        }
        val p = name.lastIndexOf(".") + 1
        if (p < 1) {
            return false
        }
        val ext = name.substring(p).toLowerCase()
        for (o in SUPPORTED_EXT) {
            if (o == ext) {
                return true
            }
        }
        return false
    }

    private class FilenameComparator : Comparator<File> {
        override fun compare(f1: File, f2: File): Int {
            return f1.name.compareTo(f2.name)
        }
    }

    private class DirFirstComparator : Comparator<File> {
        override fun compare(f1: File, f2: File): Int {
            return if (f1.isDirectory == f2.isDirectory)
                0
            else if (f1.isDirectory && !f2.isDirectory)
                -1
            else
                1
        }
    }
}

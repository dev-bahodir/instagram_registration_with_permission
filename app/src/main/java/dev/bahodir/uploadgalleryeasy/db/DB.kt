package dev.bahodir.uploadgalleryeasy.db

import dev.bahodir.uploadgalleryeasy.user.User

interface DB {

    fun add(user: User)
    fun edit(user: User)
    fun delete(user: User)
    fun getList() : MutableList<User>
}
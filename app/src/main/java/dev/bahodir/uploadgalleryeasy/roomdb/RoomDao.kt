package dev.bahodir.uploadgalleryeasy.roomdb

import androidx.room.*
import dev.bahodir.uploadgalleryeasy.user.UserForRoom
import io.reactivex.Flowable

@Dao
interface RoomDao {

    @Insert
    fun add(userForRoom: UserForRoom)

    @Update
    fun edit(userForRoom: UserForRoom)

    @Delete
    fun delete(userForRoom: UserForRoom)

    @Query("select * from userforroom")
    fun getList(): Flowable<MutableList<UserForRoom>>
}
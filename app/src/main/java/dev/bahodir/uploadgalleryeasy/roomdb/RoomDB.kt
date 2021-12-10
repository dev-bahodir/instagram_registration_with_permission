package dev.bahodir.uploadgalleryeasy.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.bahodir.uploadgalleryeasy.user.UserForRoom

@Database(entities = [UserForRoom::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        private var roomDB: RoomDB? = null

        @Synchronized
        fun getInstance(context: Context): RoomDB {
            if (roomDB == null) {
                roomDB = Room.databaseBuilder(context, RoomDB::class.java, "helper")
                    .allowMainThreadQueries()
                    .build()
            }
            return roomDB!!
        }
    }
}
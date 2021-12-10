package dev.bahodir.uploadgalleryeasy.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class UserForRoom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,
    var name: String? = null,
    var number: String? = null,
    var country: String? = null,
    var address: String? = null,
    var password: String? = null,
    var photo: String? = null
): Serializable
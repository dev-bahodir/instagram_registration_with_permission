package dev.bahodir.uploadgalleryeasy.user

import android.net.Uri
import java.io.Serializable

data class User(
    var id: Int? = null,
    var name: String? = null,
    var number: String? = null,
    var country: String? = null,
    var address: String? = null,
    var password: String? = null,
    var photo: String? = null
): Serializable
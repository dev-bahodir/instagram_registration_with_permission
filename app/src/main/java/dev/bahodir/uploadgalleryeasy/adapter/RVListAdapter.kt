package dev.bahodir.uploadgalleryeasy.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.bahodir.uploadgalleryeasy.databinding.RvLayoutBinding
import dev.bahodir.uploadgalleryeasy.user.UserForRoom
import java.io.File

class RVListAdapter(var listener: OnItemTouchClickListener) : ListAdapter<UserForRoom, RVListAdapter.VH>(DU()) {

    interface OnItemTouchClickListener {
        fun more(user: UserForRoom, position: Int, view: View)
    }

    class DU : DiffUtil.ItemCallback<UserForRoom>() {
        override fun areItemsTheSame(oldItem: UserForRoom, newItem: UserForRoom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserForRoom, newItem: UserForRoom): Boolean {
            return oldItem == newItem
        }
    }

    inner class VH(var binding: RvLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(user: UserForRoom, position: Int) {

            binding.apply {
                name.text = user.name
                number.text = user.number
                profilePhoto.setImageURI(Uri.fromFile(File(user.photo)))

                clicked.setOnClickListener {
                    listener.more(user, position, it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(RvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position), position)
    }
}
package com.example.mongodbrealmcourse.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.PlateItemRecyclerViewBinding
import com.example.mongodbrealmcourse.model.`object`.PlateNumberObject
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper

class PlateLibAdapter : ListAdapter<PlateNumberObject, PlateLibAdapter.MyViewHolder>(MyDiffCallback()) {
    class MyViewHolder(private val binding: PlateItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlateNumberObject) {
            if(item.imgPlate != ""){
                Glide.with(this.itemView.context)
                    .load(item.imgPlate)
                    .into(binding.ivPlateRcv)
            } else {
                binding.ivPlateRcv.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.bienso))
            }
            binding.tvPlateRcv.text = item.infoPlate.uppercase()
            binding.tvTimeCreateRcv.text = binding.root.context.getString(R.string.entry_time)+" "+item.dateCreate
            binding.root.setOnClickListener {
                AnimationHelper.scaleAnimation(it, object : VoidCallback {
                    override fun execute() {
                        val bundle = Bundle()
                        bundle.putParcelable("plateObject", item)
                        binding.root.findNavController().navigate(R.id.action_main_fragment_to_blankFragment, bundle)
                    }
                }, 0.98f)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PlateItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class MyDiffCallback : DiffUtil.ItemCallback<PlateNumberObject>() {
        override fun areItemsTheSame(oldItem: PlateNumberObject, newItem: PlateNumberObject): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlateNumberObject, newItem: PlateNumberObject): Boolean {
            return oldItem == newItem
        }
    }

}

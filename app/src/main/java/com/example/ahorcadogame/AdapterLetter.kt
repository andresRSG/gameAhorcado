package com.example.ahorcadogame

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ahorcadogame.databinding.ElementLetterBinding
import com.example.ahorcadogame.models.LettersCheck

class AdapterLetter(private val context:Context, private var letters :List<LettersCheck>):
    RecyclerView.Adapter<AdapterLetter.ViewHolder>() {

        class ViewHolder(view:ElementLetterBinding):RecyclerView.ViewHolder(view.root){
            val tvLetter = view.tvLetter;
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ElementLetterBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(letters[position].isCheck){
            holder.tvLetter.text = letters[position].letter.toString()
        }

    }

    override fun getItemCount(): Int = letters.size

    @SuppressLint("NotifyDataSetChanged")
    public fun updateList(letters :List<LettersCheck>){
        this.letters = letters
        notifyDataSetChanged()
    }

}
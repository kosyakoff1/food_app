package com.kosyakoff.foodapp.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.kosyakoff.foodapp.R

object RecipesRowBinding {

    @BindingAdapter("coilImage")
    @JvmStatic
    fun coilImage(view: ImageView, imageUrl: String) {
        view.load(imageUrl) {
            crossfade(600)

        }
    }

    @BindingAdapter("numberAsString")
    @JvmStatic
    fun numberAsString(view: TextView, value: Int) {
        view.text = value.toString()
    }

    @BindingAdapter("isVegan")
    @JvmStatic
    fun isVegan(view: View, isVegan: Boolean) {
        when (view) {
            is TextView -> {
                view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        if (isVegan) R.color.green else R.color.mediumGray
                    )
                )
            }
            is ImageView -> {
                view.setColorFilter(
                    ContextCompat.getColor(
                        view.context,
                        if (isVegan) R.color.green else R.color.mediumGray
                    )
                )
            }
        }

    }

}
package com.kosyakoff.foodapp.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.fragments.favorites.FavouriteRecipesFragmentDirections
import com.kosyakoff.foodapp.ui.fragments.recipes.RecipesFragmentDirections
import com.kosyakoff.foodapp.util.extensions.showToast
import org.jsoup.Jsoup
import java.lang.Exception

object RecipesRowBindingAdapters {

    @BindingAdapter("coilImage")
    @JvmStatic
    fun coilImage(view: ImageView, imageUrl: String) {
        view.load(imageUrl) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
    }

    @JvmStatic
    @BindingAdapter("app:textToParse")
    fun textToParse(textView: TextView, inputString: String?) {
        inputString?.let {
            val parsedString = Jsoup.parse(it).text()
            textView.text = parsedString
        }
    }

    @JvmStatic
    @BindingAdapter("app:visibilityBaseOnDataPresence")
    fun visibilityBaseOnDataPresence(view: View, data: List<Any>?) {
        view.isVisible = data.isNullOrEmpty().not()
    }

    @JvmStatic
    @BindingAdapter("app:visibilityBaseOnDataPresenceReverse")
    fun visibilityBaseOnDataPresenceReverse(view: View, data: List<Any>?) {
        view.isVisible = data.isNullOrEmpty()
    }

    @BindingAdapter("numberAsString")
    @JvmStatic
    fun numberAsString(view: TextView, value: Int) {
        view.text = value.toString()
    }

    @BindingAdapter("app:onFavoriteRecipesClicked")
    @JvmStatic
    fun onFavoriteRecipesClicked(recipesLayout: ConstraintLayout, recipe: FoodRecipe) {
        recipesLayout.setOnClickListener {
            try {
                val action =
                    FavouriteRecipesFragmentDirections.actionFavouriteRecipesFragmentToDetailsActivity(
                        recipe
                    )
                recipesLayout.findNavController().navigate(action)
            } catch (ex: Exception) {
                recipesLayout.context.showToast(ex.message.toString())
            }
        }
    }

    @BindingAdapter("app:onRecipesClicked")
    @JvmStatic
    fun onRecipesClicked(recipesLayout: ConstraintLayout, recipe: FoodRecipe) {
        recipesLayout.setOnClickListener {
            try {
                val action =
                    RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(recipe)
                recipesLayout.findNavController().navigate(action)
            } catch (ex: Exception) {
                recipesLayout.context.showToast(ex.message.toString())
            }
        }
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
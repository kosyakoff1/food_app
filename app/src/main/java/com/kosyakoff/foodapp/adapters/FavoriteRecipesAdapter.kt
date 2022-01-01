package com.kosyakoff.foodapp.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kosyakoff.foodapp.databinding.FavoriteRecipesRowLayoutBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.util.RecipesDiffUtilCallback

class FavoriteRecipesAdapter :
    ListAdapter<FoodRecipe, FavoriteRecipesAdapter.FavoritesViewHolder>(RecipesDiffUtilCallback) {

    private lateinit var selectionTracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder =
        FavoritesViewHolder.from(parent)

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) =
        holder.bind(
            currentList[position],
            selectionTracker.isSelected(getItemId(position))
        )

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLong()
    }

    fun setSelectionTracker(tracker: SelectionTracker<Long>) {
        selectionTracker = tracker
    }

    class FavoritesItemKeyProvider(private val adapter: FavoriteRecipesAdapter) :
        ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int): Long =
            adapter.currentList[position].id.toLong()

        override fun getPosition(key: Long): Int =
            adapter.currentList.indexOfFirst { it.id.toLong() == key }
    }

    class FavoritesItemDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as FavoritesViewHolder)
                    .getItemDetails()
            }
            return null
        }
    }

    class FavoritesViewHolder(private val binding: FavoriteRecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }

        fun bind(
            recipe: FoodRecipe,
            isActivated: Boolean
        ) {
            binding.recipe = recipe
            binding.executePendingBindings()

            binding.root.isActivated = isActivated
        }

        companion object {
            fun from(parent: ViewGroup): FavoritesViewHolder = FavoritesViewHolder(
                FavoriteRecipesRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}
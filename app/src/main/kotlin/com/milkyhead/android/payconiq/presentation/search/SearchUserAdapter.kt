package com.milkyhead.android.payconiq.presentation.search

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.milkyhead.android.payconiq.core.dp
import com.milkyhead.android.payconiq.databinding.GithubUserItemViewBinding
import com.milkyhead.android.payconiq.databinding.LoadingItemViewBinding
import com.milkyhead.android.payconiq.domain.model.UserModel


internal class SearchUserAdapter(
    private val onLoadMoreListener: (Int) -> Unit,
    private val onItemClicked: (UserModel, View) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<UserModel>()

    var isLoading = false
        set(value) {
            field = value
            notifyItemChanged(itemCount)
        }

    var noMoreData = false

    private val itemDecoration = object : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (itemCount == 1 && getItemViewType(0) == VIEW_TYPE_LOADING) {
                outRect.top = parent.height / 2
            } else {
                val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
                val dp16 = 16.dp
                if (spanIndex == 0) {
                    outRect.left = dp16
                    outRect.right = dp16 / 4
                } else {
                    outRect.right = dp16
                    outRect.left = dp16 / 4
                }
                outRect.top = dp16 / 4
                outRect.bottom = dp16 / 4
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_LOADING) {
            LoadingViewHolder(LoadingItemViewBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(GithubUserItemViewBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(items[position])
        }

        if (!noMoreData && !isLoading && position == itemCount - 1) {
            onLoadMoreListener(itemCount)
        }
    }

    override fun getItemCount(): Int {
        var count = items.size
        if (isLoading) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        val count = items.size
        return if (isLoading) {
            if (position < count) {
                VIEW_TYPE_DATA
            } else {
                VIEW_TYPE_LOADING
            }
        } else {
            VIEW_TYPE_DATA
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(itemDecoration)
        (recyclerView.layoutManager as? GridLayoutManager)?.let { layoutManager ->
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

                override fun getSpanSize(position: Int): Int {
                    val itemViewType = getItemViewType(position)
                    return if (itemViewType == VIEW_TYPE_LOADING) 2 else 1
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeItemDecoration(itemDecoration)
    }

    fun addSearchData(list: List<UserModel>, totalCount: Int) {
        // check the flow give redundant data(last list) or not
        if (items.size == list.size && items.containsAll(list)) return

        val currentItemsSize = items.size
        if (list.isNotEmpty()) {
            items.addAll(list)
            val newItemsSize = items.size
            notifyItemRangeChanged(currentItemsSize, newItemsSize - currentItemsSize)
        }

        if (itemCount >= totalCount) {
            noMoreData = true
        }
    }

    fun clearData() {
        val currentItemsSize = items.size
        items.clear()
        notifyItemRangeRemoved(0, currentItemsSize)
    }


    private inner class ItemViewHolder(
        private val binding: GithubUserItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userModel: UserModel) {
            binding.userItemAvatar.load(userModel.avatar)
            binding.userItemUsername.text = userModel.username

            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClicked(items[pos], binding.userItemAvatar)
                }
            }
        }
    }

    private class LoadingViewHolder(
        binding: LoadingItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root)


    private companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_DATA = 1
    }
}
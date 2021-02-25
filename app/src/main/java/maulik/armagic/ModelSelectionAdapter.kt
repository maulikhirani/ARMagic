package maulik.armagic

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import maulik.armagic.databinding.ListItemArModelBinding

class ModelSelectionAdapter(val callback: (ArModel) -> Unit) :
    ListAdapter<ArModel, ModelSelectionAdapter.ModelSelectionViewHolder>(ArModelDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    var tracker: SelectionTracker<String>? = null

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelSelectionViewHolder {
        val binding = ListItemArModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModelSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelSelectionViewHolder, position: Int) {
        val arModel = getItem(position)
        tracker?.let {
            holder.bind(arModel, it.isSelected(arModel.resourceId.toString()))
        }
    }

    inner class ModelSelectionViewHolder(val binding: ListItemArModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String =
                    getItem(adapterPosition).resourceId.toString()

                override fun inSelectionHotspot(e: MotionEvent) = true
            }

        fun bind(arModel: ArModel, isSelected: Boolean) {
            binding.modelContainer.setBackgroundResource(
                if (isSelected) {
                    R.drawable.model_selected_bg
                } else {
                    R.drawable.model_unselected_bg
                }
            )
            binding.tvModelName.text = arModel.name
            binding.ivModel.setImageResource(arModel.imageResource)
        }
    }

}

class ArModelDiffCallback : DiffUtil.ItemCallback<ArModel>() {
    override fun areItemsTheSame(oldItem: ArModel, newItem: ArModel): Boolean {
        return oldItem.resourceId == newItem.resourceId
    }

    override fun areContentsTheSame(oldItem: ArModel, newItem: ArModel): Boolean {
        return oldItem.resourceId == newItem.resourceId
    }
}

class ArModelKeyLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as ModelSelectionAdapter.ModelSelectionViewHolder).getItemDetails()
        }
        return null
    }
}

class ArModelKeyProvider(private val adapter: ModelSelectionAdapter) :
    ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String =
        adapter.currentList[position].resourceId.toString()

    override fun getPosition(key: String): Int =
        adapter.currentList.indexOfFirst { it.resourceId.toString() == key }
}

class ArModelSelectionPredicate() :
    SelectionTracker.SelectionPredicate<String>() {

    var tracker: SelectionTracker<String>? = null

    override fun canSetStateForKey(key: String, nextState: Boolean): Boolean {
        if (!nextState && tracker?.selection?.size() == 1) {
            return false
        }
        return true
    }

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
        if (!nextState && tracker?.selection?.size() == 1) {
            return false
        }
        return true
    }

    override fun canSelectMultiple(): Boolean {
        return false
    }

}
package app.screenreader.widgets

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import app.screenreader.R
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

abstract class ListFragment: BaseFragment() {

    private val recyclerView get() = view?.findViewById<RecyclerView>(R.id.recyclerView)
    override fun getLayoutId(): Int {
        return R.layout.view_list
    }

    abstract val items: List<Any>
    abstract val adapter: ListDelegationAdapter<List<Any>>

    open var decoration: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.items = items
        recyclerView?.adapter = adapter

        if (decoration) {
            recyclerView?.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }
}
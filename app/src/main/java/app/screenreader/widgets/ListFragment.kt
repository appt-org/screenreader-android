package app.screenreader.widgets

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import app.screenreader.R
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.view_list.*

abstract class ListFragment: BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.view_list
    }

    abstract fun getItems(): List<Any>

    abstract fun getAdapter(): ListDelegationAdapter<List<Any>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = getAdapter()
        adapter.items = getItems()

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }
}
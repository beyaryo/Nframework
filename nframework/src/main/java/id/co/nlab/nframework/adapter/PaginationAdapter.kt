package id.co.nlab.nframework.adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


abstract class PaginationAdapter<Holder : RecyclerView.ViewHolder, DataClass>(
        private var holder: Class<Holder>,
        private var data: ArrayList<DataClass>,
        private var itemView: Int,
        private var loadingView: Int,
        private var headerView: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor(holder: Class<Holder>, data: ArrayList<DataClass>, itemView: Int, loadingView: Int) :
            this(holder, data, itemView, loadingView, -751)

    enum class State {
        LOAD_NEXT, LOADING, ERROR, END
    }

    private var activity: Activity? = null
    private var recyclerView: RecyclerView? = null
    private val paginationData = ArrayList<Any>()

    private val TAG_LOAD = 0
    private val TAG_DATA = 1
    private val TAG_HEADER = 2
    private var state = State.LOAD_NEXT

    private val HEADER_IS_NULL = -751

    init {
        this.paginationData.add(headerView != HEADER_IS_NULL)
        this.data.mapTo(this.paginationData){}
        this.paginationData.add(state)
    }

    fun setRecyclerView(activity: Activity?, recyclerView: RecyclerView?, threshold: Int) {
        this.activity = activity
        this.recyclerView = recyclerView
        val manager = LinearLayoutManager(recyclerView?.context, LinearLayoutManager.VERTICAL, false)

        recyclerView?.apply {
            layoutManager = manager
            adapter = this@PaginationAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val count = manager.itemCount
                    val lastItem = manager.findLastVisibleItemPosition()

                    if (state == State.LOAD_NEXT && count <= lastItem + threshold) {
                        state = State.LOADING
                        loadMore(count - if (headerView == HEADER_IS_NULL) 1 else 2)
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return if (state == State.END) paginationData.size - 1 else paginationData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && paginationData[0] is Boolean) TAG_HEADER
        else if (position >= paginationData.size) TAG_LOAD
        else TAG_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TAG_HEADER -> HeaderHolder(LayoutInflater.from(parent.context).inflate(headerView, parent, false))
            TAG_LOAD -> LoadMoreHolder(LayoutInflater.from(parent.context).inflate(loadingView, parent, false))
            else -> holder.getConstructor(View::class.java)
                    .newInstance(LayoutInflater.from(parent.context).inflate(itemView, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val index = if (headerView == HEADER_IS_NULL) position else position - 1
        when {
            paginationData[position] is Boolean -> onHeaderBind(holder.itemView)
            paginationData[position] is State -> {
                if (paginationData[position] == State.ERROR) onError(holder.itemView)
                else if (paginationData[position] != State.END) onLoading(holder.itemView)
            }
            else -> onBind(holder as Holder, paginationData[position] as DataClass, index)
        }
    }

    fun loadNext() {
        setState(State.LOAD_NEXT)
    }

    fun error() {
        setState(State.ERROR)
    }

    fun end() {
        setState(State.END)
    }

    private fun setState(state: State) {
        this.state = state

        paginationData.remove(paginationData.size - 1)
        paginationData.add(this.state)

        activity?.runOnUiThread {
            if (state == State.LOAD_NEXT) recyclerView?.scrollToPosition(paginationData.size - 1)
            notifyDataSetChanged()
        }
    }

    fun refresh() {
        refresh(false)
    }

    fun refresh(clearAll: Boolean) {
        this.state = State.LOAD_NEXT
        Log.d("TAG", "This is refresh ${paginationData.size}")

        if (clearAll) {
            activity?.runOnUiThread { recyclerView?.scrollToPosition(0) }
            paginationData.clear()
            paginationData.add(headerView != HEADER_IS_NULL)
        } else
            paginationData.remove(paginationData.size - 1)

        data.subList(paginationData.size, data.size).mapTo(paginationData){}
        paginationData.add(this.state)

        activity?.runOnUiThread { notifyDataSetChanged() }
    }

    fun getState(): State = state

    open fun onHeaderBind(itemView: View) {}
    abstract fun onBind(holder: Holder, data: DataClass, Index: Int)
    abstract fun onLoading(itemView: View)
    abstract fun onError(itemView: View)
    abstract fun loadMore(offset: Int)

    private inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private inner class LoadMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
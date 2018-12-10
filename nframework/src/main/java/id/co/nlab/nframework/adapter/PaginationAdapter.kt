package id.co.nlab.nframework.adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


// must add Any so ArrayList.addAll can be satisfied
abstract class PaginationAdapter<Holder : RecyclerView.ViewHolder, DataClass: Any>(
        private var holder: Class<Holder>,
        private var data: ArrayList<DataClass>,
        private var itemView: Int,
        private var loadingView: Int,
        private var headerView: Int
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor(holder: Class<Holder>, data: ArrayList<DataClass>, itemView: Int, loadingView: Int) :
            this(holder, data, itemView, loadingView, -751)

    enum class State {
        LOAD_NEXT, LOADING, ERROR, END
    }

    private var activity: Activity? = null
    private var recyclerView: RecyclerView? = null
    private val paginationData = ArrayList<Any>()

    private val TAG_STATE = 0
    private val TAG_DATA = 1
    private val TAG_HEADER = 2
    private var state = State.LOAD_NEXT

    private val HEADER_IS_NULL = -751

    init {
        if (headerView != HEADER_IS_NULL) this.paginationData.add(true)
        this.paginationData.addAll(this.data)
        this.paginationData.add(state)
    }

    fun setRecyclerView(activity: Activity, recyclerView: RecyclerView, threshold: Int) {
        this.activity = activity
        this.recyclerView = recyclerView
        val manager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            layoutManager = manager
            adapter = this@PaginationAdapter

            addOnScrollListener(object: RecyclerView.OnScrollListener(){
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
        else if (position >= paginationData.size - 1) TAG_STATE
        else TAG_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TAG_HEADER -> DefaultHolder(LayoutInflater.from(parent.context).inflate(headerView, parent, false))
            TAG_STATE -> DefaultHolder(LayoutInflater.from(parent.context).inflate(loadingView, parent, false))
            else -> holder.getConstructor(View::class.java)
                    .newInstance(LayoutInflater.from(parent.context).inflate(itemView, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val index = if (headerView == HEADER_IS_NULL) position else position - 1

        when {
            paginationData[position] is Boolean -> onHeaderBind(holder.itemView)
            paginationData[position] is State -> {
                if (state == State.ERROR) onError(holder.itemView)
                else if (state != State.END) onLoading(holder.itemView)
            }
            else -> onBind(holder as Holder, paginationData[position] as DataClass, index)
        }
    }

    fun loadNext() = setState(State.LOAD_NEXT)

    fun error() = setState(State.ERROR)

    fun end() = setState(State.END)

    private fun setState(state: State) {
        this.state = state

        paginationData.removeAt(paginationData.size - 1)
        paginationData.add(this.state)

        activity?.runOnUiThread {
            if (state == State.LOAD_NEXT) {
                val firstVisiblePosition = (recyclerView?.layoutManager as LinearLayoutManager)
                        .findFirstCompletelyVisibleItemPosition() - 1
                val size = paginationData.size

                recyclerView?.scrollToPosition(size - (size - firstVisiblePosition))
            }
            notifyDataSetChanged()
        }
    }

    fun getState(): State = state

    fun refresh(clearAll: Boolean = false) {
        this.state = State.LOAD_NEXT

        if (clearAll) {
            activity?.runOnUiThread { recyclerView?.scrollToPosition(0) }
            paginationData.clear()
            if (headerView != HEADER_IS_NULL) paginationData.add(true)
        } else
            paginationData.removeAt(paginationData.size - 1)

        paginationData.addAll(data.subList(paginationData.size, data.size))
        paginationData.add(this.state)

        activity?.runOnUiThread { notifyDataSetChanged() }
    }

    open fun onHeaderBind(itemView: View) {}
    abstract fun onBind(holder: Holder, data: DataClass, Index: Int)
    abstract fun onLoading(itemView: View)
    abstract fun onError(itemView: View)
    abstract fun loadMore(offset: Int)

    private inner class DefaultHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
}
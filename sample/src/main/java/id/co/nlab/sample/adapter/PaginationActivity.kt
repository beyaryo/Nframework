package id.co.nlab.sample.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import id.co.nlab.nframework.adapter.PaginationAdapter
import id.co.nlab.sample.R
import kotlinx.android.synthetic.main.activity_pagination.*
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.coroutines.experimental.launch

class PaginationActivity: AppCompatActivity() {

    private val samples = ArrayList<SampleModel>()
    private var tempError = true

    private val adapter by lazy { object: PaginationAdapter<SampleHolder, SampleModel>(
            SampleHolder::class.java, samples, R.layout.item_sample, R.layout.item_loading
    ){

        override fun onBind(holder: SampleHolder, data: SampleModel, Index: Int) {
            holder.bind(data)
        }

        override fun onLoading(itemView: View) {
            itemView.apply {
                loading.visibility = View.VISIBLE
                error_layout.visibility = View.GONE
            }
        }

        override fun onError(itemView: View) {
            itemView.apply {
                loading.visibility = View.GONE
                error_layout.visibility = View.VISIBLE
                btn_retry.setOnClickListener { loadNext() }
            }
        }

        override fun loadMore(offset: Int) {
            loadData(offset)
        }
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagination)

        setupView()
    }

    private fun setupView(){
        adapter.setRecyclerView(this, list, 13)
        swipe.setOnRefreshListener{
            adapter.end()

            launch {
                Thread.sleep(2500)

                samples.clear()
                for(i in 0 until 20) samples.add(SampleModel(i, "User Name-$i"))

                runOnUiThread {
                    adapter.refresh(true)
                    swipe.isRefreshing = false
                }
            }
        }
    }

    private fun loadData(offset: Int){
        launch {
            Thread.sleep(2500)

            if (tempError && offset > 50) {
                adapter.error()
                tempError = false
            } else if (offset > 100) {
                adapter.end()
            } else {
                for (i in offset until offset + 20)
                    samples.add(SampleModel(i, "User Name-$i"))

                adapter.refresh()
            }
        }
    }
}
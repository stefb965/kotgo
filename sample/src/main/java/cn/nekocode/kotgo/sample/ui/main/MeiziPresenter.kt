package cn.nekocode.kotgo.sample.ui.main

import android.app.Activity
import android.os.Bundle
import cn.nekocode.kotgo.component.rx.RxBus
import cn.nekocode.kotgo.component.rx.bindAction
import cn.nekocode.kotgo.component.rx.bindLifecycle
import cn.nekocode.kotgo.component.rx.onUI
import cn.nekocode.kotgo.component.ui.BasePresenter
import cn.nekocode.kotgo.sample.data.dto.Meizi
import cn.nekocode.kotgo.sample.data.repo.MeiziRepo
import cn.nekocode.kotgo.sample.event.LoadFinishedEvent
import cn.nekocode.kotgo.sample.ui.page2.Page2Fragment
import rx.Observable
import java.util.*

/**
 * Created by nekocode on 2015/11/20.
 */
class MeiziPresenter() : BasePresenter(), Contract.Presenter {
    var view: Contract.View? = null
    val meiziList = ArrayList<Meizi>()
    val meiziListadapter = MeiziListAdapter(meiziList)

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        view = getParent() as Contract.View
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val getMeizi = MeiziRepo.getMeizis(50, 1).bindLifecycle(this)
                .flatMap {
                    RxBus.send(LoadFinishedEvent())

                    meiziList.clear()
                    meiziList.addAll(it)
                    Observable.empty<Unit>()
                }.share()

        val refreshList = onUI<Unit> { meiziListadapter.notifyDataSetChanged() }

        // Trigger
        getMeizi.bindAction(refreshList)

        meiziListadapter.onMeiziItemClickListener = {
            Page2Fragment.push(fragAct!!, it)
        }

        // You should not control the view on presenter's onCreate() because
        // when the screen rotates the presenter recreates more quickly than
        // the view
        // view?.doSth()
    }

    override fun getAdapter() = meiziListadapter
}
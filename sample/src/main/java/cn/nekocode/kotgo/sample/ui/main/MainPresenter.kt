package cn.nekocode.kotgo.sample.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.nekocode.kotgo.component.rx.RxBus
import cn.nekocode.kotgo.component.ui.KtPresenter
import cn.nekocode.kotgo.sample.data.DO.Meizi
import cn.nekocode.kotgo.sample.data.repo.MeiziRepo
import cn.nekocode.kotgo.sample.event.LoadFinishedEvent
import cn.nekocode.kotgo.sample.ui.page2.Page2Presenter
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class MainPresenter() : KtPresenter<Contract.View>(), Contract.Presenter {
    companion object {
        const val REQUEST_CODE_PAGE2 = 1
    }

    var view: Contract.View? = null
    @State var meiziList = ArrayList<Meizi>()
    val adapter = MeiziListAdapter(meiziList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)

        Observable.just(meiziList)
                .flatMap {
                    if (it.size <= 0) {
                        MeiziRepo.getMeizis(50, 1)

                    } else {
                        Observable.just(it)
                    }
                }
                .bindLifecycle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    meiziList.clear()
                    meiziList.addAll(it)
                    adapter.notifyDataSetChanged()
                    RxBus.send(LoadFinishedEvent())

                }
    }

    override fun onViewCreated(view: Contract.View?, savedInstanceState: Bundle?) {
        this.view = view

        adapter.let {
            view?.setupAdapter(it)

            it.onMeiziItemClickListener = { meizi ->
                Page2Presenter.pushForResult(this, REQUEST_CODE_PAGE2, meizi)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState ?: return)
    }

    override fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PAGE2 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val rltMeizi = data.getParcelableExtra<Meizi>(Page2Presenter.KEY_RLT_MEIZI)
                    view?.toast("You clicked the photo: ${rltMeizi.id}")
                }
            }
        }
    }
}
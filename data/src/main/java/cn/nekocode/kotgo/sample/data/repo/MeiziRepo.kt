package cn.nekocode.kotgo.sample.data.repo

import cn.nekocode.kotgo.sample.data.DO.Meizi
import cn.nekocode.kotgo.sample.data.exception.GankServiceException
import cn.nekocode.kotgo.sample.data.service.Api.Gank
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object MeiziRepo {

    // Bussines Logic
    fun getMeizis(count: Int, pageNum: Int): Observable<List<Meizi>> =
            Gank.API.getMeizi(count, pageNum)
                    .subscribeOn(Schedulers.io())
                    .map {
                        if (pageNum == 1) Paper.book().write("meizis", it.results)
                        it.results
                    }
                    .onErrorResumeNext { err: Throwable ->
                        if (pageNum != 1) throw GankServiceException(err.message)

                        // Fetch data from local cache
                        val meiziList: List<Meizi> = Paper.book().read("meizis")
                                ?: throw GankServiceException(err.message)
                        Observable.just(meiziList)
                    }

}
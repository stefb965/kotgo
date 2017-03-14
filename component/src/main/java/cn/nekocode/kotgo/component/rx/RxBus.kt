package cn.nekocode.kotgo.component.rx

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
object RxBus {
    private val bus: PublishSubject<Any> = PublishSubject.create()

    fun send(o: Any) {
        bus.onNext(o)
    }

    fun toObserverable(): Observable<Any> {
        return bus
    }

    fun <T> toObserverable(classType: Class<T>): Observable<T> {
        return bus.ofType(classType)
    }
}

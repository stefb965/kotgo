package cn.nekocode.kotgo.component.rx

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.ObservableTransformer

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class RxLifecycle {
    enum class Event {
        CREATE, ATTACH, CREATE_VIEW, RESTART, START, RESUME,
        PAUSE, STOP, DESTROY_VIEW, DETACH, DESTROY
    }

    val behavior: BehaviorSubject<Event> = BehaviorSubject.create()

    fun onCreate() { behavior.onNext(Event.CREATE) }
    fun onAttach() { behavior.onNext(Event.ATTACH) }
    fun onCreateView() { behavior.onNext(Event.CREATE_VIEW) }
    fun onRestart() { behavior.onNext(Event.RESTART) }
    fun onStart() { behavior.onNext(Event.START) }
    fun onResume() { behavior.onNext(Event.RESUME) }
    fun onPause() { behavior.onNext(Event.PAUSE) }
    fun onStop() { behavior.onNext(Event.STOP) }
    fun onDestroyView() { behavior.onNext(Event.DESTROY_VIEW) }
    fun onDetach() { behavior.onNext(Event.DETACH) }
    fun onDestroy() { behavior.onNext(Event.DESTROY) }

    interface Impl {
        val lifecycle: RxLifecycle

        fun <T> Observable<T>.bindLifecycle(): Observable<T> {
            return compose(CheckUIDestroiedTransformer<T>(lifecycle))
        }
    }

    private class CheckUIDestroiedTransformer<T>(val lifecycle: RxLifecycle) :
            ObservableTransformer<T, T> {

        override fun apply(upstream: Observable<T>): ObservableSource<T> {
            return upstream.takeUntil(
                    lifecycle.behavior.skipWhile {
                        it != RxLifecycle.Event.DESTROY_VIEW &&
                                it != RxLifecycle.Event.DESTROY &&
                                it != RxLifecycle.Event.DETACH
                    }
            )
        }
    }
}
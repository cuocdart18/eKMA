package com.app.ekma.base.fragment

interface FragmentLifeCycleObservable {
    fun addObserver(o: FragmentLifecycleObserver)
}
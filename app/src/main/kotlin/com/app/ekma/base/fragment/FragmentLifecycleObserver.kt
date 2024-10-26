package com.app.ekma.base.fragment

interface FragmentLifecycleObserver {
    fun onFragmentViewCreated() {}
    fun onFragmentStart() {}
    fun onFragmentResume() {}
    fun onFragmentPause() {}
    fun onFragmentStop() {}
    fun onFragmentDestroy() {}
    fun onFragmentDestroyView() {}
}
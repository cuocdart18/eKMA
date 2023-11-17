package com.app.ekma.common.pattern.singleton

import com.app.ekma.data.models.Profile

object ProfileSingleton {
    private var profile = Profile("", "", "", "")

    operator fun invoke() = profile

    fun setData(profile: Profile) = synchronized(this) {
        ProfileSingleton.profile = profile
    }

    fun release() {
        profile = Profile("", "", "", "")
    }
}
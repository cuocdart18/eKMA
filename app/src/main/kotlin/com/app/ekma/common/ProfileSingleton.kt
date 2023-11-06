package com.app.ekma.common

import com.app.ekma.data.models.Profile

object ProfileSingleton {
    private var profile = Profile("", "", "", "")

    operator fun invoke() = profile

    fun setData(profile: Profile) = synchronized(this) {
        this.profile = profile
    }

    fun release() {
        this.profile = Profile("", "", "", "")
    }
}
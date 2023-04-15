package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import javax.inject.Inject

class PeriodRepository @Inject constructor(
) : BaseRepositories() {
    override val TAG: String = PeriodRepository::class.java.simpleName
}
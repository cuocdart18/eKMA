package com.example.kmatool.data.repositories

import com.example.kmatool.base.repositories.BaseRepositories
import com.example.kmatool.data.services.MiniStudentLocalService
import javax.inject.Inject

class MiniStudentRepository @Inject constructor(
    private val miniStudentLocalService: MiniStudentLocalService
) : BaseRepositories() {
    override val TAG: String = MiniStudentRepository::class.java.simpleName
}
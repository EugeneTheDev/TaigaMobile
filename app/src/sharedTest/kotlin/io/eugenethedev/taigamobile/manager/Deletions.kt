package io.eugenethedev.taigamobile.manager

import okhttp3.Request

interface Deletions {
    fun TaigaTestInstanceManager.deleteTestUser() {
        Request.Builder()
            .delete()
            .apiEndpoint("users/$userId")
            .withAuth()
            .build()
            .execute()
            .successOrThrow()
    }

    fun TaigaTestInstanceManager.deleteTestProject() {
        Request.Builder()
            .delete()
            .apiEndpoint("projects/$projectId")
            .withAuth()
            .build()
            .execute()
            .successOrThrow()
    }
}

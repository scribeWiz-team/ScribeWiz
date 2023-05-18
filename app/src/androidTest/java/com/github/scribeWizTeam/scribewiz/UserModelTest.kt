package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.UserModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class UserModelTest {

    @Test
    fun putAndRetrieveUserInDBSucceed(){
        val id = "test-id"
        val user = UserModel(id)

        runBlocking {
            user.updateInDB().await()
        }
        val ret = UserModel.user(id)
        assertTrue(ret.isSuccess)

        ret.onSuccess {
            assertEquals(id, it.id)
        }

        user.delete()
    }

    @Test
    fun tryToRetrieveInvalidUserFromDBFails(){
        val id = "INVALID-test-id"

        val ret = UserModel.user(id)
        assertTrue(ret.isFailure)
    }
}
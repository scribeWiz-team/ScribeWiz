package com.github.scribeWizTeam.scribewiz

import com.github.scribeWizTeam.scribewiz.models.UserModel
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class UserModelTest {

    @Test
    fun putAndRetrieveUserInDBSucceed(){
        val id = "test-id"
        val user = UserModel(id)

        user.updateInDB()
        val ret = UserModel.user(id)
        assertTrue(ret.isSuccess)

        ret.onSuccess {
            assertEquals(id, it.id)
        }

        user.delete()
    }
}
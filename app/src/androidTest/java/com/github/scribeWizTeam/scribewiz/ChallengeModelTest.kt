package com.github.scribeWizTeam.scribewiz;

import com.github.scribeWizTeam.scribewiz.models.ChallengeModel;
import org.junit.Assert
import org.junit.Assert.assertEquals

import org.junit.Test;

class ChallengeModelTest {

    @Test
    fun putAndRetrieveChallengeInDBSucceed(){
        val id = "test-id"
        val challenge = ChallengeModel(id)

        challenge.updateInDB()
        val ret = ChallengeModel.challenge(id)
        Assert.assertTrue(ret.isSuccess)

        assertEquals(id, ret.getOrNull()?.id ?: "")
        challenge.delete()
    }
}

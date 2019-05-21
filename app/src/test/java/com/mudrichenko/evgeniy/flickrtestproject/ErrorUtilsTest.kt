package com.mudrichenko.evgeniy.flickrtestproject

import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations

class ErrorUtilsTest {

    @InjectMocks
    val mErrorUtils: ErrorUtils? = null

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun testError() {


        println(mErrorUtils!!.getErrorMessage(12, "hui"))
        println(mErrorUtils!!.getErrorMessage(-3232, "SLDF OSKFSLF;SD FLSDFSDF,LSDFSDF SDFS DFS DSD SD SD SDF "))
        println(mErrorUtils!!.getErrorMessage(0, null))

    }

}
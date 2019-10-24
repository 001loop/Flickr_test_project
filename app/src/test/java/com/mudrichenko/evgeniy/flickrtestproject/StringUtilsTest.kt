package com.mudrichenko.evgeniy.flickrtestproject

import com.google.android.gms.maps.model.LatLng
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils
import net.bytebuddy.matcher.ElementMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.ArgumentMatchers.anyInt
import android.R.bool




@RunWith(MockitoJUnitRunner::class)
class StringUtilsTest {

    @Mock
    lateinit var mStringUtils: StringUtils

    @Before
    fun setup() {
        initMocks(this)
    }

    @Test
    fun testLatLngFromString() {
        // todo test latLng
        val latLngCorrectExample = "lat/lng: (-0.51213 , 12.655123)"
        val latLngWithoutCommaExample = "lat/lng: (-0.51213 | 12.655123)"
        val latLngNullExample = null
        val latLngEmptyExample = "lat/lng: ( , )"
        //Assert.assertEquals(mStringUtils.getLatLngFromString(latLngWithoutCommaExample), null)
        //Assert.assertEquals(mStringUtils.getLatLngFromString(latLngNullExample), null)
        //Assert.assertEquals(mStringUtils.getLatLngFromString(latLngEmptyExample), null)

    }


}
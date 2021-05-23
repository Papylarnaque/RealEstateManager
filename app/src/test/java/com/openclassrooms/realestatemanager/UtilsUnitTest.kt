package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.test.mock.MockContext
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.Utils.EUR_DOLLAR_RATIO
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

internal class UtilsTest {

    @Test
    fun convertDollarToEuro_isTrue() {
        val euros = 100
        val dollars = (euros / EUR_DOLLAR_RATIO).roundToInt()
        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }

    @Test
    fun convertEurosToDollar_isTrue() {
        val dollars = 100
        val euros = (dollars * EUR_DOLLAR_RATIO).roundToInt()
        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }


    @Test
    fun todayDateFormatting_isTrue() {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date = format.format(Date())
        assertEquals(date, Utils.getTodayDate())
    }

    @Test
    fun isConnectionAvailable() {
        val cm = mock(ConnectivityManager::class.java)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val ni = mock(NetworkInfo::class.java)
            Mockito.`when`(ni.isConnectedOrConnecting).thenReturn(true)
            Mockito.`when`(cm.activeNetworkInfo).thenReturn(ni)
        } else {
            val nc = mock(NetworkCapabilities::class.java)
            Mockito.`when`(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
            Mockito.`when`(cm.getNetworkCapabilities(cm.activeNetwork)).thenReturn(nc)
        }

        val context = mock(MockContext::class.java)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(cm)

        Assert.assertTrue(Utils.isInternetAvailable(context))
    }
}
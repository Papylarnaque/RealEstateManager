package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.wifi.WifiManager;

import org.junit.Test;
import org.mockito.Mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsUnitTest {

    @Mock private Context context;

    @Test
    public void testConvertDollarToEuro() {
        int dollars = 100;
        int euros = (int) (100 * 0.812);

        assertEquals(euros, Utils.convertDollarToEuro(dollars));
    }

    @Test
    public void testGetTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        assertEquals(Utils.getTodayDate(), dateFormat.format(new Date()));
    }

    @Test
    public void testIsInternetAvailable() {
        // TODO Manage testing Internet Availability

        //  create mock
//        Context context = mock(Context.class);

        // define return value for method getUniqueId()
//        when(Utils.isInternetAvailable(context)).thenReturn(true);
//        assertEquals(Utils.isInternetAvailable(context), true);
    }

}

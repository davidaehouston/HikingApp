package data;


import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RouteViewModelTest extends Point {

    private RouteViewModel routeViewModelTest;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
    }
}
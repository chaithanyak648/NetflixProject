package com.example.netflix.Activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import com.example.netflix.R;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SplashScreenTest extends TestCase {
@Rule
public ActivityTestRule<SplashScreen>activityTestRule=new ActivityTestRule<>(SplashScreen.class);
private SplashScreen screen=null;
Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(SigninActivity.class.getName(),null,false);

    private Instrumentation getInstrumentation() {
        return null;
    }

    @Before
    public void setUp() throws Exception {
        screen=activityTestRule.getActivity();
        super.setUp();
    }
    @Test
    public void test()
    {
        View v=screen.findViewById(R.id.forgotpasswordtextview);
        Activity signin=getInstrumentation().waitForMonitorWithTimeout(monitor,5000);
        assertNotNull(signin);
    }

    public void tearDown() throws Exception {
        screen=null;
    }
}
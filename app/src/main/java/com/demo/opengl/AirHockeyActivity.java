package com.demo.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.demo.opengl.opengl.AirHockeyRenderer;


public class AirHockeyActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager systemService = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo deviceConfigurationInfo = systemService.getDeviceConfigurationInfo();
        final boolean supportEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;

        if (supportEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            rendererSet = true;
        } else {
            Toast.makeText(this, "no", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(glSurfaceView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}


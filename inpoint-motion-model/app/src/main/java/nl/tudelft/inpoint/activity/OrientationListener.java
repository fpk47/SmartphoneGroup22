package nl.tudelft.inpoint.activity;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class OrientationListener implements SensorEventListener {

    private TextView orientationX, orientationY, orientationZ;

    public OrientationListener(TextView x, TextView y, TextView z) {
        this.orientationX = x;
        this.orientationY = y;
        this.orientationZ = z;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        orientationX.setText(event.values[0] + "");
        orientationY.setText(event.values[1] + "");
        orientationZ.setText(event.values[2] + "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

package nl.tudelft.inpoint.localization;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import nl.tudelft.inpoint.R;


import nl.tudelft.inpoint.Globals;

public class DirectionListener implements View.OnClickListener, SensorEventListener {

    private float[] mGravity;
    private float[] mGeomagnetic;

    @Override
    public void onClick(View view) {
        Globals.DIRECTION_ZERO = Globals.DIRECTION_CURRENT;
        Log.d("DIRECTION ZERO: ", Globals.DIRECTION_ZERO + "");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Log.d("Z-Value: ", sensorEvent.values[2] + "");
//        float x = - sensorEvent.values[2];
//        if (x < 0) {
//            Globals.DIRECTION_CURRENT = (int) ((x + 2) * 180);
//        } else {
//            Globals.DIRECTION_CURRENT = (int) ((x) * 180);
//        }
//        Globals.DIRECTION = Globals.DIRECTION_CURRENT - Globals.DIRECTION_ZERO;
//        if (Globals.DIRECTION > 360) {
//            Globals.DIRECTION = Globals.DIRECTION - 360;
//        } else if (Globals.DIRECTION < 0) {
//            Globals.DIRECTION = Globals.DIRECTION + 360;
//        }
////        Log.i("VALUES: ", x + ", " + Globals.DIRECTION_CURRENT + ", " + Globals.DIRECTION_ZERO  + ", " + Globals.DIRECTION );
//        int id = Globals.ACTIVITY.getResources().getIdentifier("fabDirection", "id", Globals.ACTIVITY.getPackageName());
//


        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values.clone();
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values.clone();
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0];
                TextView rotationView = (TextView) Globals.VIEW.findViewById(nl.tudelft.inpoint.R.id.rotationView);

                Globals.DIRECTION_CURRENT = azimuthToDegrees(azimut);
                Globals.DIRECTION = (Globals.DIRECTION_CURRENT - Globals.DIRECTION_ZERO + 360) % 360;
                rotationView.setText(Globals.DIRECTION + "");


                FloatingActionButton button = (FloatingActionButton) Globals.VIEW.findViewById(nl.tudelft.inpoint.R.id.fabDirection);
                if (Globals.DIRECTION > 315 || Globals.DIRECTION <= 45) {
                    button.setImageResource(nl.tudelft.inpoint.R.drawable.ic_arrow_forward);
                } else if (Globals.DIRECTION > 45 && Globals.DIRECTION <= 135) {
                    button.setImageResource(nl.tudelft.inpoint.R.drawable.ic_arrow_downward);
                } else if (Globals.DIRECTION > 135 && Globals.DIRECTION <= 225) {
                    button.setImageResource(nl.tudelft.inpoint.R.drawable.ic_arrow_back);
                } else {
                    button.setImageResource(nl.tudelft.inpoint.R.drawable.ic_arrow_upward);
                }
            }

        }
    }

    private int azimuthToDegrees(float azimuth) {
        float r = azimuth + (float) Math.PI;
        return (int) (r / (2 * (float) Math.PI) * 360);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

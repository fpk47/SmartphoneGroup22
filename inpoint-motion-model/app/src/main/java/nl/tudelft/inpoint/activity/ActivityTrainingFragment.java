package nl.tudelft.inpoint.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;

public class ActivityTrainingFragment extends Fragment {

    private SensorManager mSensorManager;
    private Sensor mOrientation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_training, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Globals.ACTIVITY = getActivity();
        Globals.VIEW = getView();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        setOrientationListener();
    }

    public void setOrientationListener() {
        TextView directionX = (TextView) getView().findViewById(R.id.directionX);
        TextView directionY = (TextView) getView().findViewById(R.id.directionY);
        TextView directionZ = (TextView) getView().findViewById(R.id.directionZ);
        mSensorManager.registerListener(new OrientationListener(directionX, directionY, directionZ), mOrientation, SensorManager.SENSOR_DELAY_GAME);
    }

}

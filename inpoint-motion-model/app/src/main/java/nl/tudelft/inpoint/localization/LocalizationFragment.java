package nl.tudelft.inpoint.localization;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;
import nl.tudelft.inpoint.training.ResetController;

public class LocalizationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_localization, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Globals.ACTIVITY = getActivity();
        Globals.VIEW = getView();
        initRooms();
        setLocalizeController();
        setResetListener();
        setDirectionListener();
        setActivityListener();
        setMotionListener();
    }

    private void initRooms() {
        float probability = 1f / Globals.NUMBER_OF_ROOMS;
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            int id = Globals.RESOURCES.getIdentifier("room" + i, "id", Globals.PACKAGE_NAME);
            setRoom((TextView) getView().findViewById(id), probability);
        }
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            Globals.POSTERIOR[i] = probability;
        }
        Globals.MAX_PRIOR = probability;
    }

    private void setRoom(TextView room, float probability) {
        int p = Math.round(probability * 100);
        room.setBackgroundColor(getResources().getColor(Globals.getColor(p)));
        room.setText(p + "");
    }

    private void setLocalizeController() {
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fabLocalize);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        LocalizeController controller = new LocalizeController(getView());
        getActivity().registerReceiver(controller, intentFilter);
        button.setOnClickListener(controller);
    }

    private void setResetListener() {
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fabReset);
        button.setOnClickListener(new ResetController(getView()));
    }

    private void setDirectionListener() {
        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        DirectionListener directionListener = new DirectionListener();

        mSensorManager.registerListener(directionListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(directionListener, mMagnetic, SensorManager.SENSOR_DELAY_UI);

        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fabDirection);
        button.setOnClickListener(directionListener);
    }

    private void setActivityListener() {
        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new ActivityListener(), mAccelerometer, mSensorManager.SENSOR_DELAY_FASTEST);
    }

    private void setMotionListener() {
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fabMotion);
        button.setOnClickListener(new MotionListener());
    }

}

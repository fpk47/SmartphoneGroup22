package nl.tudelft.inpoint.training;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;
import nl.tudelft.inpoint.training.RecordController;
import nl.tudelft.inpoint.training.RoomController;

public class TrainingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Globals.ACTIVITY = getActivity();
        Globals.VIEW = getView();
        Globals.SELECTED_ROOM = (TextView) getView().findViewById(R.id.room1);
        Globals.SELECTED_ROOM.setBackgroundColor(getResources().getColor(R.color.mapSelected));
        setRoomControllers();
        setRecordController();
    }

    private void setRecordController() {
        FloatingActionButton button = (FloatingActionButton) getView().findViewById(R.id.fab);
        button.setOnClickListener(new RecordController(button));
    }

    private void setRoomControllers() {
        TextView room;
        Resources r = getResources();
        String name = getActivity().getPackageName();
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            int id = r.getIdentifier("room" + i, "id", name);
            room = (TextView) getView().findViewById(id);
            room.setOnClickListener(new RoomController(getView(), room));
        }
    }

}

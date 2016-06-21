package nl.tudelft.inpoint.training;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;

public class RoomController implements View.OnClickListener {

    private TextView roomView;
    private View view;

    public RoomController(View view, TextView roomID) {
        this.view = view;
        this.roomView = roomID;
    }

    @Override
    public void onClick(View view) {
        if (!Globals.RECORDING) {
            Globals.SELECTED_ROOM.setBackgroundColor(Globals.MAP_DEFAULT_COLOR);
            Globals.SELECTED_ROOM = roomView;
            roomView.setBackgroundColor(Globals.MAP_SELECTED_COLOR);
            ((TextView) this.view.findViewById(R.id.selectedRoom)).setText(roomView.getText());
        }
    }
}

package nl.tudelft.inpoint.training;

import android.view.View;
import android.widget.TextView;

import nl.tudelft.inpoint.Globals;

public class ResetController implements View.OnClickListener {

    private View view;

    public ResetController(View view) {
        this.view = view;
    }

    @Override
    public void onClick(View view) {
        float probability = 1f / Globals.NUMBER_OF_ROOMS;
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            int id = Globals.RESOURCES.getIdentifier("room" + i, "id", Globals.PACKAGE_NAME);
            setRoom((TextView) this.view.findViewById(id), probability);
        }
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            Globals.POSTERIOR[i] = probability;
        }
        Globals.MAX_PRIOR = probability;
    }

    private void setRoom(TextView room, float probability) {
        int p = Math.round(probability * 100);
        room.setBackgroundColor(Globals.RESOURCES.getColor(Globals.getColor(p)));
        room.setText(p + "");
    }

}
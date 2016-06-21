package nl.tudelft.inpoint.localization;


import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;

public class MotionListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        Globals.RECORDING_MOTION = !Globals.RECORDING_MOTION;
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fabMotion);

        if (Globals.RECORDING_MOTION) {
            button.setBackgroundTintList(ColorStateList.valueOf(Globals.MAP_SELECTED_COLOR));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Globals.MAP_DEFAULT_COLOR));
        }
    }
}

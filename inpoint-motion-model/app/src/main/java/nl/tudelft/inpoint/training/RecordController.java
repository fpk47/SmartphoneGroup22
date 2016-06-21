package nl.tudelft.inpoint.training;

import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.wifi.WifiManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;
import nl.tudelft.inpoint.training.RSSRecorder;
import nl.tudelft.inpoint.SQLiteHelper;

public class RecordController implements View.OnClickListener {

    private FloatingActionButton button;

    public RecordController(FloatingActionButton floatingActionButton) {
        Globals.RECORDER = new RSSRecorder();
        this.button = floatingActionButton;
    }

    @Override
    public void onClick(View view) {
        toggleIcon();
     }

    private void toggleIcon() {
        if (Globals.RECORDING) {
            button.setImageResource(R.drawable.ic_play_arrow);
            button.setBackgroundTintList(ColorStateList.valueOf(Globals.MAP_DEFAULT_COLOR));
            storeRSStoDatabase();
            Globals.ACTIVITY.unregisterReceiver(Globals.RECORDER);
        } else {
            Globals.RSS_VALUES.clear();
            button.setImageResource(R.drawable.ic_pause);
            button.setBackgroundTintList(ColorStateList.valueOf(Globals.MAP_SELECTED_COLOR));

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            Globals.ACTIVITY.registerReceiver(Globals.RECORDER, intentFilter);
            Thread t = new Thread(Globals.RECORDER);
            t.start();
        }
        Globals.RECORDING = !Globals.RECORDING;
    }

    private void storeRSStoDatabase() {
        int roomID = Integer.parseInt(Globals.SELECTED_ROOM.getText().toString());

        Iterator iter = Globals.RSS_VALUES.keySet().iterator();
        while (iter.hasNext()) {
            String mac = (String) iter.next();
            String table = SQLiteHelper.encodeMAC(mac);
            Globals.DATABASE.createFrequencyTable(table);
            Globals.DATABASE.createGaussianTable(table);
            Globals.DATABASE.updateRSSFrequencies(table, roomID, Globals.RSS_VALUES.get(mac));
        }

        Globals.DATABASE.filterAP();
    }
}

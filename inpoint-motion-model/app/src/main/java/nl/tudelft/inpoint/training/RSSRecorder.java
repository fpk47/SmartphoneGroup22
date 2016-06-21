package nl.tudelft.inpoint.training;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.R;

public class RSSRecorder extends BroadcastReceiver implements Runnable {

    private Handler handler;

    public RSSRecorder() {
        handler = new Handler();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScanResult> list = Globals.WIFI_MANAGER.getScanResults();

        for (ScanResult r : list) {
            int level = Math.abs(r.level);
            if (!Globals.RSS_VALUES.containsKey(r.BSSID)) {
                int[] rss = new int[101];
                Globals.RSS_VALUES.put(r.BSSID, rss);
            }
            int[] rss = Globals.RSS_VALUES.get(r.BSSID);
            rss[level]++;
            Globals.RSS_VALUES.put(r.BSSID, rss);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) Globals.VIEW.findViewById(R.id.sampleCounter)).setText(++Globals.SAMPLE_COUNTER + "");
            }
        });

        if (Globals.RECORDING) {
            Log.i("Scan status:", "Starting another scan");
            Globals.WIFI_MANAGER.startScan();
        }
    }

    @Override
    public void run() {
        Globals.SAMPLE_COUNTER = 0;
        Globals.RECORDING = true;
        Globals.WIFI_MANAGER.startScan();
    }
}



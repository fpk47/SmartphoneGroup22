package nl.tudelft.inpoint.localization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.tudelft.inpoint.Globals;
import nl.tudelft.inpoint.SQLiteHelper;

public class LocalizeController extends BroadcastReceiver implements View.OnClickListener {

    private View view;
    private boolean scanning = false;

    public LocalizeController(View view) {
        this.view = view;
    }

    @Override
    public void onClick(View v) {
        if (Globals.WIFI_MANAGER.startScan()) {
            setStatus("SCANNING");
            scanning = true;
        }
    }

    private void setStatus(String status) {
        int id = Globals.RESOURCES.getIdentifier("status", "id", Globals.PACKAGE_NAME);
        TextView room = (TextView) view.findViewById(id);
        room.setText(status);
    }

    private void showAPList(List<ScanResult> list) {
        String res = "";
        for (ScanResult r : list)
            res += r.level + ", ";
        Log.i("", res);
    }

    private void debugProbabilities(float[] probabilities, String mac) {
        for (int j = 1; j <= Globals.NUMBER_OF_ROOMS; j++) {
            Log.i(j + " After Bayes", "Probability: " + probabilities[j] + ", Room: " + Globals.POSTERIOR[j] + ", MAC: " + mac);
        }
    }

    private void debugStatisticalParameters(float[][] stats) {
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            String mean = Float.toString(stats[i][0]);
            String sd = Float.toString(stats[i][1]);
            Log.i("Room " + i + ": ", "mean: " + mean + ", sd: " + sd);
        }
    }

    private void displayTopMAC(List<ScanResult> list) {
        for (int i = 0; i < 3; i++) {
            ScanResult r = list.get(i);
            int id = Globals.RESOURCES.getIdentifier("mac" + (i + 1), "id", Globals.PACKAGE_NAME);
            TextView room = (TextView) view.findViewById(id);
            room.setText(r.BSSID + ", " + r.level);
        }
    }

    private void applyBayes(float[] probabilities) {
//        float sum = 0;
//        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++)
//            sum += probabilities[i];
//        if (sum == 0) return;

        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++)
            Globals.POSTERIOR[i] = Globals.POSTERIOR[i] * probabilities[i];
    }

    private void normalizePosterior() {
        float sum = 0;
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++)
            sum += Globals.POSTERIOR[i];
        if (sum == 0)
            return;
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            Globals.POSTERIOR[i] = Globals.POSTERIOR[i] / sum;
            if (Globals.POSTERIOR[i] > Globals.MAX_PRIOR)
                Globals.MAX_PRIOR = Globals.POSTERIOR[i];
        }
    }

    private void showPosterior() {
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            int id = Globals.RESOURCES.getIdentifier("room" + i, "id", Globals.PACKAGE_NAME);
            TextView room = (TextView) view.findViewById(id);
            setRoom(room, Globals.POSTERIOR[i]);
            room.setText("" + Math.round(Globals.POSTERIOR[i] * 100));
        }
    }

    private void setRoom(TextView room, float probability) {
        int p = Math.round(probability * 100);
        room.setBackgroundColor(Globals.RESOURCES.getColor(Globals.getColor(p)));
        room.setText(p + "");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && scanning) {
            List<ScanResult> unfilteredList = Globals.WIFI_MANAGER.getScanResults();
            List<String> filter = Globals.DATABASE.getSignificantAccessPoints();
            List<ScanResult> list = new ArrayList<>();
            for (ScanResult s : unfilteredList)
                if (filter.contains(SQLiteHelper.encodeMAC(s.BSSID)))
                    list.add(s);
            if (list.isEmpty())
                list = unfilteredList;
            Collections.sort(list, new ScanResultComparator());
            displayTopMAC(list);
            Iterator<ScanResult> iter = list.iterator();
            while (iter.hasNext() && Globals.MAX_PRIOR <= 0.95) {
                ScanResult r = iter.next();
                String mac = SQLiteHelper.encodeMAC(r.BSSID);
                float[][] statistics = Globals.DATABASE.getStatisticalParameters(mac);
                debugStatisticalParameters(statistics);
                int level = -r.level;
                float[] probabilities = Globals.DATABASE.getRSSProbabilities(mac, level);
                if (probabilities != null) {
                    applyBayes(probabilities);
                    normalizePosterior();
                    showPosterior();
                }
            }
            setStatus("CHANGED");
            scanning = false;
        }
    }
}

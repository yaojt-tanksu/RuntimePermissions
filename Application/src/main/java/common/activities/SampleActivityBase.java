package common.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogWrapper;


/**
 * Base launcher activity, to handle most of the common plumbing for samples.
 */
public class SampleActivityBase extends AppCompatActivity {

    public static final String TAG = "SampleActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Ready");
    }
}

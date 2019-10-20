package com.colorworld.manbocash.tutorial;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class StepWorker extends Worker implements SensorEventListener{
    public StepWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    boolean isEntered = false;
    int steps = 0;
    long timestamp = 0;

    @NonNull
    @Override
    public Result doWork() {
        Log.i("sensor", "upload 시작");
        uploadStep();
        Log.i("sensor", "upload 끝");
        Log.i("sensor", "isEntered: "+ isEntered);

        if ( isEntered ) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    public void uploadStep() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 서버에 저장
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String time = Timestamp.now().toString();
        Map<String, Object> stepData = new HashMap<>();
        stepData.put("timestamp", Timestamp.now() );
        stepData.put("steps", steps);
        DocumentReference stepsRef = db.collection("users").document(uid).collection("steps").document(time);
        stepsRef.set(stepData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("db", "user saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "user save 실패 :"+ e);
            }
        });

        Log.i("sensor", "user idToken=> "+uid+ "/"+ user.getUid() );
        sensorManager.unregisterListener(this);
        return;

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("sensor", "sensor event => "+ event.values[0]);
        isEntered = true;
        steps = (int)event.values[0];
        timestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}

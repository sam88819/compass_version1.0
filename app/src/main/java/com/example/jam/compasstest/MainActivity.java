package com.example.jam.compasstest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView compassLogo;
    private TextView degreeText;
    private SensorManager sensorManager;
    private float lastDegree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //组件绑定
        compassLogo = (ImageView)findViewById(R.id.compassView);
        degreeText = (TextView)findViewById(R.id.degreeText);
        //传感器绑定
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor magetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listerner,magetic,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listerner,accelerometer,SensorManager.SENSOR_DELAY_GAME);

    }

    /**
     * 定义监听器接口
     *
     */
    private SensorEventListener listerner =  new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticValues = new float[3];


        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accelerometerValues = event.values.clone();
            }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues = event.values.clone();
            }

            float[] matrix = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(matrix,null,accelerometerValues,magneticValues);
            SensorManager.getOrientation(matrix,values);

            float rotateDegree = -(float)Math.toDegrees(values[0]);
            if(Math.abs(rotateDegree - lastDegree)>1){
                RotateAnimation animation = new RotateAnimation(lastDegree,rotateDegree,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation.setDuration(200);
                animation.setFillAfter(true);
                compassLogo.startAnimation(animation);
                lastDegree = rotateDegree;
            }
            String positionstr = null;
            if (22.5 >= lastDegree || lastDegree >= 337.5) {
                positionstr = "北";
            } else if (22.5 < lastDegree && lastDegree <= 67.5) {
                positionstr = "东北";
            } else if (67.5 < lastDegree && lastDegree <= 112.5) {
                positionstr = "东";
            } else if (112.5 < lastDegree && lastDegree <= 157.5) {
                positionstr = "东南";
            } else if (157.5 < lastDegree && lastDegree <= 202.5) {
                positionstr = "南";
            } else if (202.5 < lastDegree && lastDegree <= 247.5) {
                positionstr = "西南";
            } else if (247.5 < lastDegree && lastDegree <= 292.5) {
                positionstr = "西";
            }else if(292.5 < lastDegree && lastDegree <= 337.5){
                positionstr = "西北";
            }
            degreeText.setText(positionstr+String.valueOf(Math.floor(lastDegree))+'°');
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    //避免传感器过多消耗电量和占用资源，而且指南针功能不需要在后台操作，所以需要在Activity onDestory()时释放资源
    @Override
    protected void onDestroy() {
        if(sensorManager != null)
                sensorManager.unregisterListener(listerner);
        super.onDestroy();
    }
}

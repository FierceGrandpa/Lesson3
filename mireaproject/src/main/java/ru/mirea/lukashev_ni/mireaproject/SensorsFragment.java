package ru.mirea.lukashev_ni.mireaproject;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class SensorsFragment extends Fragment implements SensorEventListener, LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private SensorManager sensorManager;
    private Sensor compassSensor;
    private LocationManager locationManager;

    private TextView directionTextView;

    public SensorsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);
        directionTextView = rootView.findViewById(R.id.directionTextView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int coarsePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (coarsePermission == PackageManager.PERMISSION_GRANTED && finePermission == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        sensorManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float azimuth = event.values[0];

            String direction = getDirectionFromAzimuth(azimuth);

            directionTextView.setText("Направление на север: " + direction);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String direction = getDirectionFromSun(latitude, longitude);

        directionTextView.setText("Направление на север: " + direction);
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private String getDirectionFromAzimuth(float azimuth) {
        if (azimuth >= 0 && azimuth < 45) {
            return "Север";
        } else if (azimuth >= 45 && azimuth < 135) {
            return "Восток";
        } else if (azimuth >= 135 && azimuth < 225) {
            return "Юг";
        } else if (azimuth >= 225 && azimuth < 315) {
            return "Запад";
        } else {
            return "Север";
        }
    }

    private String getDirectionFromSun(double latitude, double longitude) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 6 && hourOfDay < 18) {
            return "Юг";
        } else {
            return "Север";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
            }
        }
    }
}

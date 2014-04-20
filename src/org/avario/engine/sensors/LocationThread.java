package org.avario.engine.sensors;

import org.avario.AVarioActivity;
import org.avario.R;
import org.avario.engine.SensorProducer;
import org.avario.engine.SensorThread;
import org.avario.engine.datastore.DataAccessObject;

import android.content.Context;
import android.hardware.SensorEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationThread extends SensorThread<Location> implements LocationListener {

	private final LocationManager locationManager;

	public LocationThread() {
		locationManager = (LocationManager) AVarioActivity.CONTEXT.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void run() {
		locationManager.removeUpdates(LocationThread.this);
		AVarioActivity.CONTEXT.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationThread.this);
				} catch (Exception e) {
					Toast.makeText(AVarioActivity.CONTEXT,
							AVarioActivity.CONTEXT.getApplicationContext().getString(R.string.gps_fail),
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	@Override
	public void stop() {
		locationManager.removeUpdates(LocationThread.this);
	}

	@Override
	public synchronized void onLocationChanged(final Location newLocation) {
		if (newLocation.hasAltitude() && (DataAccessObject.get().getMovementFactor() instanceof GpsMovement)) {
			DataAccessObject.get().setLastAltitude((float) newLocation.getAltitude());
		}

		DataAccessObject.get().setLastlocation(newLocation);
		SensorProducer.get().notifyGpsConsumers(DataAccessObject.get().getLastlocation());
		SensorProducer.get().notifyBaroConsumers(DataAccessObject.get().getLastAltitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// GPS signal is back. Cool
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// public static final int OUT_OF_SERVICE = 0;
		// public static final int TEMPORARILY_UNAVAILABLE = 1;
		// public static final int AVAILABLE = 2;
	}

	@Override
	protected void notifySensorChanged(SensorEvent sensorEvent) {
		// TODO Auto-generated method stub
	}

}

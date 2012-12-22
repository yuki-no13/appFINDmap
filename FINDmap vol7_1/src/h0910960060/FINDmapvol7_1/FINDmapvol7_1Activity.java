package h0910960060.FINDmapvol7_1;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FINDmapvol7_1Activity extends MapActivity {
    /** Called when the activity is first created. */

	private static final String TAG = "FINDmapvol7_1Activit";
	private static final boolean DEBUG = true;

	private static final String ACTION_LOCATION_UPDATE = "com.android.practice.map.ACTION_LOCATION_UPDATE";

	private MapController mMapController;
	private MapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initMapSet();
    }

	@Override
	protected void onResume() {
		super.onResume();
		setOverlays();
		setIntentFilterToReceiver();
		requestLocationUpdates();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setOverlays();
		removeUpdates();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	private void initMapSet(){
		//MapView objectの取得
        mMapView = (MapView)findViewById(R.id.MapView);
        //MapView#setBuiltInZoomControl()でZoom controlをbuilt-in moduleに任せる
        mMapView.setBuiltInZoomControls(true);
        //MapController objectを取得
        mMapController = mMapView.getController();
	}
	private void setOverlays(){
        //User location表示用のMyLocationOverlay objectを取得
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		//初めてLocation情報を受け取った時の処理を記載
		//試しにそのLocationにanimationで移動し、zoom levelを19に変更
        mMyLocationOverlay.runOnFirstFix(new Runnable(){
        	public void run(){
        		GeoPoint gp = mMyLocationOverlay.getMyLocation();
				mMapController.animateTo(gp);
				mMapController.setZoom(19);
        	}
        });
        //LocationManagerからのLocation update取得
		mMyLocationOverlay.enableMyLocation();

		//overlayのlistにMyLocationOverlayを登録
        List<Overlay> overlays = mMapView.getOverlays();
        overlays.add(mMyLocationOverlay);
	}



	private void setIntentFilterToReceiver(){
		final IntentFilter filter = new IntentFilter();
    	filter.addAction(ACTION_LOCATION_UPDATE);
    	registerReceiver(new LocationUpdateReceiver(), filter);
	}


	private void requestLocationUpdates(){
		final PendingIntent requestLocation = getRequestLocationIntent(this);
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        for(String providerName: lm.getAllProviders()){
			if(lm.isProviderEnabled(providerName)){
				lm.requestLocationUpdates(providerName, 0, 0, requestLocation);
				if(DEBUG){
					Toast.makeText(this, "Request Location Update", Toast.LENGTH_SHORT).show();
					if(DEBUG)Log.d(TAG, "Provider: " + providerName);
				}
			}
		}
	}

	private PendingIntent getRequestLocationIntent(Context context){
		return PendingIntent.getBroadcast(context, 0, new Intent(ACTION_LOCATION_UPDATE),
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void removeUpdates(){
    	final PendingIntent requestLocation = getRequestLocationIntent(this);
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		lm.removeUpdates(requestLocation);
		if(DEBUG)Toast.makeText(this, "Remove update", Toast.LENGTH_SHORT).show();
    }

	public class LocationUpdateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action == null){
				return;
			}
			if(action.equals(ACTION_LOCATION_UPDATE)){
				//Location情報を取得
				Location loc = (Location)intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
				if(loc != null){
					//試しにMapControllerで現在値に移動する
					mMapController.animateTo(new GeoPoint((int)(loc.getLatitude() * 1E6), (int)(loc.getLongitude() * 1E6)));
					//if(DEBUG)Toast.makeText(context, "latitude:" + loc.getLatitude() + "\nlongitude:" + loc.getLongitude(), Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

}
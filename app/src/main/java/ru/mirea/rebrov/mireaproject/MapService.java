package ru.mirea.rebrov.mireaproject;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import ru.mirea.rebrov.mireaproject.databinding.FragmentMapServiceBinding;

public class MapService extends Fragment
{
    private FragmentMapServiceBinding binding;
    private View root;
    private MapView mapView = null;
    private MyLocationNewOverlay locationNewOverlay;
    private static final int REQUEST_CODE_PERMISSION = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentMapServiceBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        Configuration.getInstance().load(root.getContext().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(root.getContext().getApplicationContext()));
        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);
        CompassOverlay compassOverlay = new CompassOverlay(root.getContext().getApplicationContext(), new InternalCompassOrientationProvider(root.getContext().getApplicationContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
        int cOARSE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fINE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (cOARSE_LOCATION == PackageManager.PERMISSION_GRANTED || fINE_LOCATION == PackageManager.PERMISSION_GRANTED)
        {
            myPosition();
        }
        //Маркер 1 - Лужники
        Marker luzhniki = new Marker(mapView);
        luzhniki.setPosition(new GeoPoint(55.715762, 37.553358));
        luzhniki.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                Toast.makeText(root.getContext().getApplicationContext(),"Лужники" + "\nул. Лужники, 24, стр. 1", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(luzhniki);
        luzhniki.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        luzhniki.setTitle("luzhniki");
        //Маркер 2 - Качалин
        Marker kachalin = new Marker(mapView);
        kachalin.setPosition(new GeoPoint(55.719506, 37.575903));
        kachalin.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                Toast.makeText(root.getContext().getApplicationContext(),"Стадион им.Г.Д.Качалина" + "\nФрунзенская наб., вл50", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(kachalin);
        kachalin.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        kachalin.setTitle("kachalin");
        //Маркер 3 - ВЭБ Арена
        Marker veb = new Marker(mapView);
        veb.setPosition(new GeoPoint(55.805931, 37.514307));
        veb.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                Toast.makeText(root.getContext().getApplicationContext(),"ВЭБ Арена" + "\n3-я Песчаная ул., 2А, стр. 2", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(veb);
        veb.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        veb.setTitle("veb arena");
        return root;
    }
    private void myPosition()
    {
        locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(root.getContext().getApplicationContext()), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(this.locationNewOverlay);
        locationNewOverlay.runOnFirstFix(new Runnable()
        {
            public void run()
            {
                try
                {
                    double latitude = locationNewOverlay.getMyLocation().getLatitude();
                    double longitude = locationNewOverlay.getMyLocation().getLongitude();
                    Log.d("coord", String.valueOf(latitude));
                    Log.d("coord", String.valueOf(longitude));
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            IMapController mapController = mapView.getController();
                            mapController.setZoom(15.0);
                            GeoPoint startPoint = new GeoPoint(latitude, longitude);
                            mapController.setCenter(startPoint);
                        }
                    });
                }
                catch (Exception e) {}
            }
        });
    }
}
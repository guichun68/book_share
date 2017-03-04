package zyzx.linke.overlay;


import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;

import zyzx.linke.R;

public class SchemeDriveOverlay extends DrivingRouteOverlay {

	public SchemeDriveOverlay(Context arg0, AMap arg1, DrivePath arg2,
							  LatLonPoint arg3, LatLonPoint arg4) {
		super(arg0, arg1, arg2, arg3, arg4, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected BitmapDescriptor getStartBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.mipmap.route_start);
	}

	@Override
	protected BitmapDescriptor getEndBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.mipmap.route_end);
	}

	@Override
	protected BitmapDescriptor getDriveBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.mipmap.cloud_car);
	}


}

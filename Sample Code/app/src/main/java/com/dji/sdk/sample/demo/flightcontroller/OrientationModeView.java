package com.dji.sdk.sample.demo.flightcontroller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.TelemetryUdpSender;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BaseThreeBtnView;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightOrientationMode;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;



/**
 * Class for Orientation mode.
 */
public class OrientationModeView extends BaseThreeBtnView {

    private static final String TELEMETRY_HOST = "192.168.100.7";
    private static final int TELEMETRY_PORT = 5005;

    private FlightController flightController;

    private String orientationMode;
    private TelemetryUdpSender telemetryUdpSender;

    public OrientationModeView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            flightController = DJISampleApplication.getAircraftInstance().getFlightController();
            telemetryUdpSender = new TelemetryUdpSender(TELEMETRY_HOST, TELEMETRY_PORT);

            flightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                    orientationMode = flightControllerState.getOrientationMode().name();
                    LocationCoordinate3D aircraftLocation = flightControllerState.getAircraftLocation();
                    String locationText = "N/A";
                    if (aircraftLocation != null) {
                        locationText = aircraftLocation.getLatitude()
                                + ", " + aircraftLocation.getLongitude()
                                + " (alt " + aircraftLocation.getAltitude() + "m)";
                        telemetryUdpSender.send(buildTelemetryPayload(aircraftLocation));
                    }
                    changeDescription("Current Orientation Mode is" + "\n"
                            + orientationMode + "\n"
                            + "Aircraft Location: " + locationText);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(ModuleVerificationUtil.isFlightControllerAvailable()) {
            flightController = DJISampleApplication.getAircraftInstance().getFlightController();
            flightController.setStateCallback(null);
        }
        if (telemetryUdpSender != null) {
            telemetryUdpSender.close();
            telemetryUdpSender = null;
        }
    }

    @Override
    protected int getMiddleBtnTextResourceId() {
        return R.string.orientation_mode_home_lock;
    }

    @Override
    protected int getLeftBtnTextResourceId() {
        return R.string.orientation_mode_course_lock;
    }

    @Override
    protected int getRightBtnTextResourceId() {
        return R.string.orientation_mode_aircraft_heading;
    }

    @Override
    protected int getDescriptionResourceId() {
        return R.string.orientation_mode_description;
    }

    @Override
    protected void handleMiddleBtnClick() {
        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            flightController = DJISampleApplication.getAircraftInstance().getFlightController();

            flightController.setFlightOrientationMode(FlightOrientationMode.HOME_LOCK,
                                                      new CommonCallbacks.CompletionCallback() {
                                                          @Override
                                                          public void onResult(DJIError djiError) {
                                                              ToastUtils.setResultToToast("Result: " + (djiError == null
                                                                                                   ? "Success"
                                                                                                   : djiError.getDescription()));
                                                          }
                                                      });
        }
    }

    @Override
    protected void handleLeftBtnClick() {
        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            flightController = DJISampleApplication.getAircraftInstance().getFlightController();

            flightController.setFlightOrientationMode(FlightOrientationMode.COURSE_LOCK,
                                                      new CommonCallbacks.CompletionCallback() {
                                                          @Override
                                                          public void onResult(DJIError djiError) {
                                                              ToastUtils.setResultToToast("Result: " + (djiError == null
                                                                                                   ? "Success"
                                                                                                   : djiError.getDescription()));
                                                          }
                                                      });
        }
    }

    @Override
    protected void handleRightBtnClick() {
        if (ModuleVerificationUtil.isFlightControllerAvailable()) {
            flightController = DJISampleApplication.getAircraftInstance().getFlightController();

            flightController.setFlightOrientationMode(FlightOrientationMode.AIRCRAFT_HEADING,
                                                      new CommonCallbacks.CompletionCallback() {
                                                          @Override
                                                          public void onResult(DJIError djiError) {
                                                              ToastUtils.setResultToToast("Result: " + (djiError == null
                                                                                                   ? "Success"
                                                                                                   : djiError.getDescription()));
                                                          }
                                                      });
        }
    }

    @Override
    public int getDescription() {
        return R.string.flight_controller_listview_orientation_mode;
    }

    private String buildTelemetryPayload(LocationCoordinate3D location) {
        long timestampMs = System.currentTimeMillis();
        return "{\"ts\":" + timestampMs
                + ",\"lat\":" + location.getLatitude()
                + ",\"lng\":" + location.getLongitude()
                + ",\"alt\":" + location.getAltitude()
                + "}";
    }
}

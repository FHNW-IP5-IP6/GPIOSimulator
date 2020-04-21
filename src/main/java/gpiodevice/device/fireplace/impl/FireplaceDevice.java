package gpiodevice.device.fireplace.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  FireplaceDevice.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import gpiodevice.component.relay.Relay;
import gpiodevice.component.relay.RelayListener;
import gpiodevice.component.relay.RelayState;
import gpiodevice.component.relay.RelayStateChangeEvent;
import gpiodevice.component.sensor.Sensor;
import gpiodevice.component.sensor.SensorListener;
import gpiodevice.component.sensor.SensorState;
import gpiodevice.component.sensor.SensorStateChangeEvent;
import gpiodevice.device.fireplace.*;

public class FireplaceDevice extends FireplaceBase {

    protected final Relay fireplaceControlRelay;
    protected final RelayState fireplaceOnRelayState;
    protected final Sensor pilotLightSensor;
    protected final SensorState pilotLightOnState;

    public FireplaceDevice(final Relay fireplaceControlRelay,
                           final Sensor pilotLightSensor){
        this(fireplaceControlRelay, RelayState.CLOSED, pilotLightSensor, SensorState.CLOSED);
    }

    public FireplaceDevice(final Relay fireplaceControlRelay, final RelayState fireplaceOnRelayState) {
        this(fireplaceControlRelay, fireplaceOnRelayState, null, SensorState.CLOSED);
    }

    public FireplaceDevice(final Relay fireplaceControlRelay){
        this(fireplaceControlRelay, RelayState.CLOSED, null, SensorState.CLOSED);
    }

    public FireplaceDevice(final Relay fireplaceControlRelay, final RelayState fireplaceOnRelayState,
                           final Sensor pilotLightSensor, final SensorState pilotLightOnState){
        this.fireplaceControlRelay = fireplaceControlRelay;
        this.fireplaceOnRelayState = fireplaceOnRelayState;
        this.pilotLightSensor = pilotLightSensor;
        this.pilotLightOnState = pilotLightOnState;

        // listen to relay changes to notify fireplace state change events
        fireplaceControlRelay.addListener(new RelayListener() {
            @Override
            public void onStateChange(RelayStateChangeEvent event) {
                if(event.getNewState() == fireplaceOnRelayState){
                    notifyListeners(new FireplaceStateChangeEvent(FireplaceDevice.this,
                            FireplaceState.OFF, FireplaceState.ON));
                }
                else{
                    notifyListeners(new FireplaceStateChangeEvent(FireplaceDevice.this,
                            FireplaceState.ON, FireplaceState.OFF));
                }
            }
        });

        // listen to fireplace pilot light sensor (if provided)
        if(pilotLightSensor != null){
            pilotLightSensor.addListener(new SensorListener() {
                @Override
                public void onStateChange(SensorStateChangeEvent event) {
                    // if the pilot light sensor no longer detects a flame,
                    // then turn off the fireplace!
                    if(!pilotLightSensor.isState(pilotLightOnState)){
                        off();
                    }

                    // notify pilot light listeners
                    notifyListeners(new FireplacePilotLightEvent(FireplaceDevice.this, isPilotLightOn()));
                }
            });
        }
    }

    @Override
    public FireplaceState getState() {
        if(fireplaceControlRelay.isState(fireplaceOnRelayState))
            return FireplaceState.ON;
        else
            return FireplaceState.OFF;
    }

    @Override
    public void setState(FireplaceState state) throws FireplacePilotLightException {

        // turn fireplace OFF
        if(state == FireplaceState.OFF){
            // toggle the state of the relay if it's current in the ON state
            if(fireplaceControlRelay.isState(fireplaceOnRelayState))
                fireplaceControlRelay.toggle();
        }

        // turn fireplace ON
        else {
            // first, if a pilot light sensor was provided, then
            // we must determine if the pilot light is lit
            if (pilotLightSensor != null && !isPilotLightOn()) {
                throw new FireplacePilotLightException();
            }

            // set the state of the relay to the ON state
            if(!fireplaceControlRelay.isState(fireplaceOnRelayState))
                fireplaceControlRelay.setState(fireplaceOnRelayState);
        }
    }

    @Override
    public boolean isPilotLightOn() {
        if(pilotLightSensor == null) return false;
        return pilotLightSensor.isState(pilotLightOnState);
    }

    @Override
    public boolean isPilotLightOff() {
        return !(isPilotLightOn());
    }

}

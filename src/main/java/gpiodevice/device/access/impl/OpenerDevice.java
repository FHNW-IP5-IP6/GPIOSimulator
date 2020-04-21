package gpiodevice.device.access.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  OpenerDevice.java
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
import gpiodevice.component.sensor.Sensor;
import gpiodevice.component.sensor.SensorListener;
import gpiodevice.component.sensor.SensorState;
import gpiodevice.component.sensor.SensorStateChangeEvent;
import gpiodevice.component.switches.Switch;
import gpiodevice.component.switches.SwitchListener;
import gpiodevice.component.switches.SwitchStateChangeEvent;
import gpiodevice.device.access.Opener;
import gpiodevice.device.access.OpenerBase;
import gpiodevice.device.access.OpenerLockChangeEvent;
import gpiodevice.device.access.OpenerLockedException;
import gpiodevice.device.access.OpenerState;
import gpiodevice.device.access.OpenerStateChangeEvent;

public class OpenerDevice extends OpenerBase implements Opener
{
    private Relay relay;
    private Sensor sensor;
    private SensorState openSensorState;
    private Switch lock = null;
    private final OpenerDevice opener = this;

    // create sensor listener
    private SensorListener sensorListener = new SensorListener() {
        @Override
        public void onStateChange(SensorStateChangeEvent event) {
            OpenerState oldState = getState(event.getOldState());
            OpenerState newState = getState(event.getNewState());
            opener.notifyListeners(new OpenerStateChangeEvent(opener, oldState, newState));
        }
    };

    // create lock switch listener
    private SwitchListener lockSwitchListener = new SwitchListener() {
        @Override
        public void onStateChange(SwitchStateChangeEvent event) {
            opener.notifyListeners(new OpenerLockChangeEvent(opener, event.getSwitch().isOn()));
        }
    };

    public OpenerDevice(Relay relay, Sensor sensor, SensorState openSensorState) {
        this.relay = relay;
        this.sensor = sensor;
        this.openSensorState= openSensorState;
        this.sensor.addListener(sensorListener);
    }

    public OpenerDevice(Relay relay, Sensor sensor, SensorState openSensorState, Switch lock) {
        this(relay,sensor, openSensorState);
        this.lock = lock;
        this.lock.addListener(lockSwitchListener);
    }

    @Override
    public void open() throws OpenerLockedException {

        // abort if the opener is locked
        if(isLocked())
            throw new OpenerLockedException(this);

        // if the open sensor determines that the door/gate is
        // not in the open state, then pulse the relay to
        // perform the open operation
        if(!sensor.isState(openSensorState)) {
            // pulse the control relay to open the garage door/gate
            relay.pulse();
        }
    }

    @Override
    public void close() throws OpenerLockedException {

        // abort if the opener is locked
        if(isLocked())
            throw new OpenerLockedException(this);

        // if the open sensor determines that the door/gate is
        // in the open state, then pulse the relay to
        // perform the close operation
        if(sensor.isState(openSensorState)) {
            // pulse the control relay to close the garage door/gate
            relay.pulse();
        }
    }

    @Override
    public OpenerState getState() {
        if(sensor.getState().equals(openSensorState))
            return OpenerState.OPEN;
        else
            return OpenerState.CLOSED;
    }

    @Override
    public boolean isLocked() {
        if(lock == null)
            return false;
        return lock.isOn();
    }

    protected OpenerState getState(SensorState sensorState) {
        if(sensorState.equals(openSensorState))
            return OpenerState.OPEN;
        else
            return OpenerState.CLOSED;
    }
}

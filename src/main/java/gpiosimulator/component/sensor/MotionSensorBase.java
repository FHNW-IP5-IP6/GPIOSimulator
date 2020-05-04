package gpiosimulator.component.sensor;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  MotionSensorBase.java
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


import java.util.Date;

import gpiosimulator.component.ComponentListener;
import gpiosimulator.component.ObserveableComponentBase;


public abstract class MotionSensorBase extends ObserveableComponentBase implements MotionSensor {

    protected Date lastMotionTimestamp = null;
    protected Date lastInactivityTimestamp = null;

    @Override
    public Date getLastMotionTimestamp() {
        return lastMotionTimestamp;
    }

    @Override
    public Date getLastInactivityTimestamp() {
        return lastInactivityTimestamp;
    }

    @Override
    public abstract boolean isMotionDetected();

    @Override
    public void addListener(MotionSensorListener... listener) {
        super.addListener(listener);
    }

    @Override
    public synchronized void removeListener(MotionSensorListener... listener) {
        super.removeListener(listener);
    }

    protected synchronized void notifyListeners(MotionSensorChangeEvent event) {
        // cache last detected timestamp
        if(event.isMotionDetected())
            lastMotionTimestamp = event.timestamp;
        else
            lastInactivityTimestamp = event.timestamp;

        // raise events to listeners
        for(ComponentListener listener : super.listeners) {
            ((MotionSensorListener)listener).onMotionStateChange(event);
        }
    }
}
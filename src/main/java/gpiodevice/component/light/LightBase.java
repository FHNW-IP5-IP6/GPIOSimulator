package gpiodevice.component.light;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  LightBase.java
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

import gpiodevice.component.ComponentListener;
import gpiodevice.component.ObserveableComponentBase;

public abstract class LightBase extends ObserveableComponentBase implements Light {

    @Override
    public abstract void on();

    @Override
    public abstract void off();

    @Override
    public abstract boolean isOn();

    @Override
    public boolean isOff() {
        return (!isOn());
    }

    @Override
    public void addListener(LightListener... listener) {
        super.addListener(listener);
    }

    @Override
    public synchronized void removeListener(LightListener... listener) {
        super.removeListener(listener);
    }

    protected synchronized void notifyListeners(LightStateChangeEvent event) {
        for(ComponentListener listener : super.listeners) {
            ((LightListener)listener).onStateChange(event);
        }
    }
}
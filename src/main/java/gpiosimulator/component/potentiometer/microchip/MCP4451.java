package gpiosimulator.component.potentiometer.microchip;

import gpiosimulator.component.potentiometer.microchip.impl.MicrochipPotentiometerBase;
import com.pi4j.io.i2c.I2CBus;

import java.io.IOException;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  MCP4451.java
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

/**
 * Pi4J-component for MCP4451.
 *
 * @author <a href="http://raspelikan.blogspot.co.at">Raspelikan</a>
 */
public class MCP4451 extends MicrochipPotentiometerBase implements MicrochipPotentiometer {

	private static final MicrochipPotentiometerChannel[] supportedChannels = new MicrochipPotentiometerChannel[] {
		MicrochipPotentiometerChannel.A, MicrochipPotentiometerChannel.B, MicrochipPotentiometerChannel.C, MicrochipPotentiometerChannel.D
	};

	/**
	 * Builds an instance which is ready to use.
	 *
	 * @param i2cBus The Pi4J-I2CBus to which the component is connected to
	 * @param pinA0 Whether the component's address pin A0 is high (true) or low (false)
	 * @param initialValue Initial value of wiper
	 * @throws IOException Thrown if communication fails or component returned a malformed result
	 */
	public MCP4451(final I2CBus i2cBus, final boolean pinA0,
                   final int initialValue) throws IOException {

		super(i2cBus, pinA0, PIN_NOT_AVAILABLE, PIN_NOT_AVAILABLE,
				MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, initialValue);

	}

	/**
	 * @return Whether component is capable of non volatile wipers (false for MCP4451)
	 */
	@Override
	public boolean isCapableOfNonVolatileWiper() {

		return false;

	}

	/**
	 * @return The maximal value at which a wiper can be (256 for MCP4451)
	 */
	@Override
	public int getMaxValue() {

		return maxValue();

	}

	/**
	 * @return The maximal value at which a wiper can be (256 for MCP4451)
	 */
	public static int maxValue() {

		return 256;

	}

	/**
	 * @return Whether this component is a potentiometer or a rheostat (false for MCP4451)
	 */
	@Override
	public boolean isRheostat() {

		return false;

	}

	/**
	 * @return All channels supported by the underlying component (A, B, C and D for MCP4451)
	 */
	@Override
	public MicrochipPotentiometerChannel[] getSupportedChannelsByDevice() {

		return supportedChannels;

	}

}

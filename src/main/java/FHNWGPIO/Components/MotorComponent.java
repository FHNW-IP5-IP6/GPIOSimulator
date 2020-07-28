package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;

public class MotorComponent {
    private final int range = 1024;
    private final int clock = 512;
    private Console console;
    private GpioPinDigitalOutput forwards;
    private GpioPinDigitalOutput backwards;
    private GpioPinPwmOutput pwmForwards;
    private GpioPinPwmOutput pwmBackwards;
    private boolean isPwm;
    private double rangeAdjustment;

    public MotorComponent(Console console, GpioPinDigitalOutput forwards, GpioPinDigitalOutput backwards) {
        this.console = console;
        this.forwards = forwards;
        this.backwards = backwards;
        isPwm = false;
        forwards.low();
        backwards.low();
    }

    public MotorComponent(Console console, GpioPinPwmOutput forwards, GpioPinPwmOutput backwards) {
        this.console = console;
        this.pwmForwards = forwards;
        this.pwmBackwards = backwards;
        isPwm = true;
        rangeAdjustment = isHardwarePwmPin(forwards.getPin()) && isHardwarePwmPin(backwards.getPin()) ? 10.24 : 1.0;
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(range);
        Gpio.pwmSetClock(clock);
    }

    public void moveForwards() {
        if (isPwm) {
            moveForwards(100);
        } else {
            backwards.low();
            forwards.high();
            console.println("motor is moving forwards with maximum power");
        }
    }

    public void moveForwards(int power) throws UnsupportedOperationException, IllegalArgumentException {
        if (!isPwm) {
            throw new UnsupportedOperationException(
                    "void moveForwards(int power) can only be used with hardware pwm pin. please use void moveForwards() instead.");
        }

        if (power < 0 || power > 100) {
            throw new IllegalArgumentException("power must be between 0 and 100 percent");
        }

        pwmBackwards.setPwm(0);
        pwmForwards.setPwm((int) (power * rangeAdjustment));
        console.println("motor is moving forwards at " + power + "%");
    }

    public void moveBackwards() {
        if (isPwm) {
            moveBackwards(100);
        } else {
            forwards.low();
            backwards.high();
            console.println("motor is moving backwards with maximum power");
        }
    }

    public void moveBackwards(int power) throws UnsupportedOperationException, IllegalArgumentException {
        if (!isPwm) {
            throw new UnsupportedOperationException(
                    "void moveBackwards(int power) can only be used with hardware pwm pin. please use void moveBackwards() instead.");
        }

        if (power < 0 || power > 100) {
            throw new IllegalArgumentException("power must be between 0 and 100 percent");
        }

        pwmForwards.setPwm(0);
        pwmBackwards.setPwm((int) (power * rangeAdjustment));
        console.println("motor is moving backwards at " + power + "%");
    }

    public void stop() {
        if (isPwm) {
            pwmForwards.setPwm(0);
            pwmBackwards.setPwm(0);
        } else {
            forwards.low();
            backwards.low();
        }
        console.println("motor stopped");
    }

    private boolean isHardwarePwmPin(Pin pin) {
        return pin.getAddress() == 12 || pin.getAddress() == 13;
    }
}

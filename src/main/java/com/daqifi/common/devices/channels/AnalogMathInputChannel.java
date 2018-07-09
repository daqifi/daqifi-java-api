package com.daqifi.common.devices.channels;

/**
 *
 */
public class AnalogMathInputChannel extends AnalogInputChannel {
    public enum Functions{
        ADD{
            public float compute(float v1, float v2){
                return v1 + v2;
            }
        },
        SUBTRACT{
            public float compute(float v1, float v2){
                return v1 - v2;
            }
        },
        MULTIPLY{
            public float compute(float v1, float v2){
                return v1 * v2;
            }
        },
        DIVIDE{
            public float compute(float v1, float v2){
                try {
                    return v1 / v2;
                }catch (ArithmeticException e){
                    return 0;
                }
            }
        };

        abstract float compute(float v1, float v2);
    }

    public AnalogInputChannel getChannel1() {
        return channel1;
    }

    public void setChannel1(AnalogInputChannel channel1) {
        this.channel1 = channel1;
    }

    public AnalogInputChannel getChannel2() {
        return channel2;
    }

    public void setChannel2(AnalogInputChannel channel2) {
        this.channel2 = channel2;
    }

    public Functions getFunc() {
        return func;
    }

    public void setFunc(Functions func) {
        this.func = func;
    }

    private AnalogInputChannel channel1;
    private AnalogInputChannel channel2;
    private Functions func;

    public AnalogMathInputChannel(String name, int index, AnalogInputChannel channel1, AnalogInputChannel channel2, Functions function) {
        super(name, index, channel1.getDevice());
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.func = function;
        setActive(true);
    }

    @Override
    public Type getType() {
        return Type.ANALOG_MATH_IN;
    }


    public void computeCurrent(long t){
        float v1 = channel1.getCurrentValue();
        float v2 = channel2.getCurrentValue();
        add(t, func.compute(v1, v2));
    }
}

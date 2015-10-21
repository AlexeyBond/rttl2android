package org.iplusplus.rttf2android.composition.samples;

public enum SampleCreators {
    SQUARE {
        @Override
        short getSampleAt(int frame, int ofFrames) {
            return (frame > (ofFrames>>1))?Short.MIN_VALUE:Short.MAX_VALUE;
        }
    },
    SINE {
        private final long min = (long)Short.MIN_VALUE;
        private final long delta = (long)Short.MAX_VALUE - min;

        @Override
        short getSampleAt(int frame, int ofFrames) {
            return (short)(min + delta * Math.sin(((float)frame) / ((float)ofFrames)));
        }
    };

    abstract short getSampleAt(int frame, int ofFrames);

    public SampleCreator get() {
        return new SampleCreator() {
            @Override
            public short sampleAt(int frame, int ofFrames) {
                return getSampleAt(frame, ofFrames);
            }
        };
    }
}

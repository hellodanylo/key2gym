package org.key2gym.business.services;

import org.joda.time.LocalTime;

/**
 * An implementation of ShiftedTimeSource that shifts
 * the current time by a value passed to the constructor.
 */
public class ShiftedTimeSource implements TimeSource {

    private long millis;

    public ShiftedTimeSource(long millis) {
        this.millis = millis;
    }

    @Override
    public LocalTime getLocalTime() {
        return new LocalTime(System.currentTimeMillis() + millis);
    }
}

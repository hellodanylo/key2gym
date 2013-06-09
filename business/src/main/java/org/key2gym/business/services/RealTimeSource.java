package org.key2gym.business.services;

import org.joda.time.LocalTime;

/**
 * An implementation of TimeSource that returns
 * the current time as is.
 */
public class RealTimeSource implements TimeSource {

    @Override
    public LocalTime getLocalTime() {
        return new LocalTime();
    }

}

package org.key2gym.business.services;

import org.joda.time.LocalTime;

/**
 * A time source is used as a transparent
 * way to specify time acquisition strategies.
 * <p/>
 * This is most useful for debugging purposes,
 * because a TimeSource might return fake,
 * pre-defined or in some other way altered
 * value .
 */
interface TimeSource {
    LocalTime getLocalTime();
}

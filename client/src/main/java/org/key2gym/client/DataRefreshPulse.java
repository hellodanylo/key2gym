/*
 * Copyright 2012-2013 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.key2gym.client;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Notifies the observers at the data refresh rate.
 *
 * This class will read the configuration and notify the observers
 * at the rate specified in the application's configuration.
 * 
 * The notification is done on the timer's thread, therefore observers
 * should return as quickly as possible.
 */
public class DataRefreshPulse extends Observable {
    protected DataRefreshPulse() {

	logger = Logger.getLogger(DataRefreshPulse.class);

	String refreshPeriod = Main.getProperties().getProperty(Main.PROPERTY_REFRESH_PERIOD);

	/* If the data refresh feature is disabled, returns. */
	if(refreshPeriod.equals("off")) {
	    logger.debug("Date refresh feature is disabled.");
	    return;
	}

        timer = new Timer("DataRefreshPulse-Timer", true);

        try {
	    timer.scheduleAtFixedRate(new DataRefreshTimerTask(), 
					new Date(), Long.valueOf(refreshPeriod));

	    logger.debug("Will pulse with the following period: " + refreshPeriod);

	} catch(NumberFormatException ex) {
	    logger.error("Data refresh property is invalid. Won't refresh.", ex);
	}
    }

    private class DataRefreshTimerTask extends TimerTask {
	@Override
	public void run() {
	    logger.trace("Pulsing!");

	    setChanged();
	    notifyObservers(null);
	}
    }

    private Timer timer;
    private Logger logger;

    /**
     * Singleton instance.
     */
    private static DataRefreshPulse instance;

    public static DataRefreshPulse getInstance() {

	if(instance == null) {
	    instance = new DataRefreshPulse();
	}

	return instance;
    }
}
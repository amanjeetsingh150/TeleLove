package com.developers.telelove.events;

/**
 * Created by Amanjeet Singh on 26/12/17.
 */

public class LaunchMessageEvent {

    public boolean isShouldLaunch() {
        return shouldLaunch;
    }

    public void setShouldLaunch(boolean shouldLaunch) {
        this.shouldLaunch = shouldLaunch;
    }

    public boolean shouldLaunch;

}

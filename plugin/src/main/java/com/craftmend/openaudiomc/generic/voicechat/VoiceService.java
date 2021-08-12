package com.craftmend.openaudiomc.generic.voicechat;

import com.craftmend.openaudiomc.generic.voicechat.driver.VoiceServerDriver;

public interface VoiceService {

    /**
     * Blocking method that tries to login to a server and establish a connection
     * @param host Server full host (eg https://joostspeeltspellen.voice.openaudiomc.net/) with a trailing slash
     * @param password Server password
     */
    void connect(String host, String password, int slots);

    void requestRestart();

    void shutdown();

    boolean isEnabled();

    int getAllowedSlots();

    int getUsedSlots();

    void fireShutdownEvents();

    void onShutdown(Runnable runnable);

    VoiceServerDriver getDriver();

    void requestCleanShutdown();

    int secondsSinceLastLogout();

    void setEnabled(boolean b);
}

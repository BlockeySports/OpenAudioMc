package com.craftmend.openaudiomc.generic.platform.interfaces;

import com.craftmend.openaudiomc.generic.networking.interfaces.NetworkingService;
import com.craftmend.openaudiomc.generic.platform.Platform;
import com.craftmend.openaudiomc.generic.storage.interfaces.Configuration;
import com.craftmend.openaudiomc.generic.voicechat.VoiceService;

public interface OpenAudioInvoker {

    boolean hasPlayersOnline();
    boolean isNodeServer();
    Platform getPlatform();
    Class<? extends NetworkingService> getServiceClass();
    TaskService getTaskProvider();
    Configuration getConfigurationProvider();
    String getPluginVersion();
    int getServerPort();
    VoiceService getVoiceService();

}

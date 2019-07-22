package com.craftmend.openaudiomc.generic.state.states;

import com.craftmend.openaudiomc.generic.state.interfaces.State;

public class AssigningRelayState implements State {

    @Override
    public String getDescription() {
        return "OpenAudioMc is making the handshake and requesting a Relay node to connect to";
    }

    @Override
    public Boolean isConnected() {
        return false;
    }

    @Override
    public Boolean canConnect() {
        return false;
    }
}


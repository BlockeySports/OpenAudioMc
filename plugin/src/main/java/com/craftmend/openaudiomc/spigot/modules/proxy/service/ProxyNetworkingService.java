package com.craftmend.openaudiomc.spigot.modules.proxy.service;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.networking.DefaultNetworkingService;
import com.craftmend.openaudiomc.generic.networking.abstracts.AbstractPacket;
import com.craftmend.openaudiomc.generic.networking.client.interfaces.PlayerContainer;
import com.craftmend.openaudiomc.generic.networking.client.objects.player.ClientConnection;
import com.craftmend.openaudiomc.generic.networking.client.objects.player.SerializableClient;
import com.craftmend.openaudiomc.generic.networking.interfaces.Authenticatable;
import com.craftmend.openaudiomc.generic.networking.interfaces.INetworkingEvents;
import com.craftmend.openaudiomc.generic.networking.interfaces.NetworkingService;
import com.craftmend.openaudiomc.generic.node.packets.ForwardSocketPacket;
import com.craftmend.openaudiomc.generic.platform.interfaces.TaskService;
import com.craftmend.openaudiomc.generic.player.SpigotPlayerAdapter;
import com.craftmend.openaudiomc.spigot.OpenAudioMcSpigot;
import com.craftmend.openaudiomc.spigot.modules.proxy.listeners.BungeePacketListener;
import com.craftmend.openaudiomc.spigot.modules.proxy.listeners.ModernPacketListener;
import com.craftmend.openaudiomc.velocity.messages.PacketPlayer;
import com.craftmend.openaudiomc.velocity.messages.StandardPacket;
import com.craftmend.openaudiomc.velocity.messages.implementations.BukkitPacketManager;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProxyNetworkingService extends NetworkingService {

    @Getter
    private final Set<INetworkingEvents> eventHandlers = new HashSet<>();
    private final DefaultNetworkingService realService = new DefaultNetworkingService();
    @Getter
    private BukkitPacketManager packetManager;
    private int packetThroughput = 0;

    public ProxyNetworkingService() {
        this.onModuleLoad();
    }

    @Override
    public void onModuleLoad() {
        packetManager = new BukkitPacketManager(OpenAudioMcSpigot.getInstance(), "openaudiomc:node");
        packetManager.registerListener(new BungeePacketListener());
        new ModernPacketListener();

        // schedule repeating task to clear the throughput
        OpenAudioMc.resolveDependency(TaskService.class).scheduleAsyncRepeatingTask(() -> {
            packetThroughput = 0;
        }, 20, 20);
    }

    @Override
    public void connectIfDown() {
        // unused in fake system
    }

    public void sendToProxy(Player player, StandardPacket packet) {
        packetManager.sendPacket(new PacketPlayer(player), packet);
    }

    @Override
    public void send(Authenticatable client, AbstractPacket packet) {
        // handle packet if it should be passed to bungee
        // forward every packet starting with PacketClient
        if (!(client instanceof ClientConnection))
            throw new UnsupportedOperationException("The bungee adapter for the networking service only supports client connections");
        if (packet.getClass().getSimpleName().startsWith("PacketClient")) {
            packet.setClient(client.getOwnerUUID());
            Player player = ((SpigotPlayerAdapter) ((ClientConnection) client).getPlayer()).getPlayer();
            packetManager.sendPacket(new PacketPlayer(player), new ForwardSocketPacket(packet));
        }

        packetThroughput++;
    }

    @Override
    public void triggerPacket(AbstractPacket abstractPacket) {
        // unused in fake system
    }

    @Override
    public ClientConnection getClient(UUID uuid) {
        return realService.getClient(uuid);
    }

    @Override
    public Collection<ClientConnection> getClients() {
        return realService.getClients();
    }

    @Override
    public int getThroughputPerSecond() {
        return packetThroughput;
    }

    @Override
    public void remove(UUID player) {
        realService.remove(player);
    }

    @Override
    public ClientConnection register(Player player, @Nullable SerializableClient importData) {
        return realService.register(player, importData);
    }

    @Override
    public ClientConnection register(PlayerContainer player, @Nullable SerializableClient importData) {
        return realService.register(player, importData);
    }

    @Override
    public ClientConnection register(ProxiedPlayer player, @Nullable SerializableClient importData) {
        return realService.register(player, importData);
    }

    public ClientConnection register(com.velocitypowered.api.proxy.Player player, @Nullable SerializableClient importData) {
        return realService.register(player, importData);
    }

    @Override
    public void stop() {
        // unused in fake system
    }

    @Override
    public Set<INetworkingEvents> getEvents() {
        return eventHandlers;
    }

    @Override
    public void addEventHandler(INetworkingEvents events) {
        eventHandlers.add(events);
    }
}

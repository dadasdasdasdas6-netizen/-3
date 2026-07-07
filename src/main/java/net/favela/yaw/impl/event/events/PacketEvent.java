package net.favela.yaw.impl.event.events;

import net.favela.yaw.impl.event.CancellableEvent;
import net.minecraft.network.protocol.Packet;

public abstract class PacketEvent extends CancellableEvent {
    private final Packet<?> packet;

    protected PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> packet() {
        return packet;
    }

    public static final class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static final class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }
}
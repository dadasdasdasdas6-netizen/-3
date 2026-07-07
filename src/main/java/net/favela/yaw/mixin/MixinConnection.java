package net.favela.yaw.mixin;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.favela.yaw.impl.event.Events;
import net.favela.yaw.impl.event.events.PacketEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class MixinConnection {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"), cancellable = true)
    private void yaw$onReceive(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        if (Events.post(new PacketEvent.Receive(packet)).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void yaw$onSend(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (Events.post(new PacketEvent.Send(packet)).isCancelled()) {
            ci.cancel();
        }
    }
}
package eu.pb4.polymer.core.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.SynchronizeRegistriesTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerConfigurationNetworkHandler.class)
public class ServerConfigurationNetworkHandlerMixin {
    @WrapOperation(method = "onSelectKnownPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/SynchronizeRegistriesTask;onSelectKnownPacks(Ljava/util/List;Ljava/util/function/Consumer;)V"))
    private void wrapWithContext(SynchronizeRegistriesTask instance, List<VersionedIdentifier> clientKnownPacks, Consumer<Packet<?>> sender, Operation<Void> original) {
        PolymerCommonUtils.executeWithNetworkingLogic(() -> {
            original.call(instance, clientKnownPacks, sender);
        });
    }
}

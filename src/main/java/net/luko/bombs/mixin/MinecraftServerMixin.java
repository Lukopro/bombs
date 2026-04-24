package net.luko.bombs.mixin;

import net.luko.bombs.data.modifiers.ModifierManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(
            method = "reloadResources",
            at = @At("RETURN")
    )
    private void bombs$afterReload(Collection<String> pSelectedIds,
                                   CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenRun(() -> {
            MinecraftServer server = (MinecraftServer)(Object)this;
            ModifierManager.INSTANCE.syncToAll(server);
        });
    }
}

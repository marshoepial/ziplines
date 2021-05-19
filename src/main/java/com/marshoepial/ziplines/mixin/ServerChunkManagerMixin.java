package com.marshoepial.ziplines.mixin;

import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @Inject(method = "shouldTickEntity", at=@At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void keepTickingZiplines(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if (entity instanceof WrappedRopeEntity) {
            if (((WrappedRopeEntity) entity).isBeingPulled()) {
                cir.setReturnValue(true);
            }
        }
    }
}

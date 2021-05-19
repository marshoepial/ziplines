package com.marshoepial.ziplines.mixin;

import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker")
public abstract class ThreadedAnvilChunkStorageEntityTrackerMixin {
    @Shadow
    private ThreadedAnvilChunkStorage field_18245;

    @Accessor
    abstract Entity getEntity();

    @Invoker
    abstract int callGetMaxTrackDistance();

    @ModifyVariable(method = "updateCameraPosition(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at=@At(value = "STORE", ordinal = 0))
    private boolean ziplineCustomUnloadRange(boolean shouldBeLoaded, ServerPlayerEntity player){
        Entity entity = getEntity();
        if (entity instanceof WrappedRopeEntity) {
            Vec3d difference = player.getPos().subtract(((WrappedRopeEntity) entity).getClosestPointToRope(player.getPos()));
            int maximumDist = Math.min(callGetMaxTrackDistance(), (((ThreadedAnvilChunkStorageMixin)field_18245).getWatchDistance() - 1)*16);

            return Math.abs(difference.x) <= maximumDist && Math.abs(difference.z) <= maximumDist && entity.canBeSpectated(player);
        }

        return shouldBeLoaded;
    }
}

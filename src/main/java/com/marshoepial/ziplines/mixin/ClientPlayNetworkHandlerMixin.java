package com.marshoepial.ziplines.mixin;

import com.marshoepial.ziplines.entity.EntityRegistrar;
import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//Mixin to support modded entities through the EntitySpawnS2CPacket.
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    private static EntityType<?> entityType;
    private static BlockPos pos;

    @Shadow private ClientWorld world;

    @Inject(method = "onEntitySpawn", at = @At(value="FIELD",
            target = "Lnet/minecraft/entity/EntityType;CHEST_MINECART:Lnet/minecraft/entity/EntityType;",
            opcode = Opcodes.GETSTATIC), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getEntityType(EntitySpawnS2CPacket packet, CallbackInfo ci, double d, double e, double f, EntityType<?> entityType) {
        ClientPlayNetworkHandlerMixin.entityType = entityType;
        pos = new BlockPos(d, e, f);
    }

    @ModifyVariable(method = "onEntitySpawn", ordinal = 0, at = @At(value = "STORE", ordinal = 36))
    private Entity handleWrappedRope(Entity variable){
        if (entityType == EntityRegistrar.WRAPPED_ROPE_ENTITY_TYPE) {
            return new WrappedRopeEntity(this.world, pos);
        }
        return variable;
    }
}

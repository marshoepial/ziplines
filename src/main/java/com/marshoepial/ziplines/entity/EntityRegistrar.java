package com.marshoepial.ziplines.entity;

import com.marshoepial.ziplines.Ziplines;
import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistrar {
    //Hey, I heard ya liked casts...
    public static final EntityType<WrappedRopeEntity> WRAPPED_ROPE_ENTITY_TYPE = (EntityType<WrappedRopeEntity>) (Object) Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(Ziplines.MOD_ID, "wrapped_rope"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, WrappedRopeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.125f, 0.375f))
                    .build());
}

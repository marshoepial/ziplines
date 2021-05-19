package com.marshoepial.ziplines.client;

import com.marshoepial.ziplines.entity.EntityRegistrar;
import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ZiplinesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new ModelProvider());
        EntityRendererRegistry.INSTANCE.register(EntityRegistrar.WRAPPED_ROPE_ENTITY_TYPE,
                (dispatcher, context) -> new WrappedRopeEntityRenderer(dispatcher));
    }
}

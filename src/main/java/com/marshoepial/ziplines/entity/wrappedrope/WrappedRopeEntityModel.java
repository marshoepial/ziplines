package com.marshoepial.ziplines.entity.wrappedrope;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class WrappedRopeEntityModel extends EntityModel<WrappedRopeEntity> {
    private final ModelPart wrappedAround;

    public WrappedRopeEntityModel() {
        this.textureHeight = 72;
        this.textureWidth = 72;

        this.wrappedAround = new ModelPart(this, 0, 0);
        wrappedAround.addCuboid(-9, -3, -9, 18, 6, 18);
    }

    @Override
    public void setAngles(WrappedRopeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        wrappedAround.render(matrices, vertices, light, overlay);
    }
}

package com.marshoepial.ziplines.entity.wrappedrope;

import com.marshoepial.ziplines.Ziplines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import static java.lang.Math.*;

public class WrappedRopeEntityRenderer extends EntityRenderer<WrappedRopeEntity> {
    private final WrappedRopeEntityModel model = new WrappedRopeEntityModel();

    public WrappedRopeEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(WrappedRopeEntity entity) {
        return new Identifier(Ziplines.MOD_ID, "textures/entity/wrapped_rope.png");
    }

    @Override
    public boolean shouldRender(WrappedRopeEntity entity, Frustum frustum, double x, double y, double z) {
        return entity.isBeingPulled() || super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public void render(WrappedRopeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        matrices.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer consumer = vertexConsumers.getBuffer(this.model.getLayer(getTexture(entity)));
        int lightAtBlock = WorldRenderer.getLightmapCoordinates(entity.world, entity.getBlockPos().up());
        this.model.render(matrices, consumer, lightAtBlock, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);

        VertexConsumer leashLayer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix = matrices.peek().getModel();

        //get the offset covered by this rope. if this is null no rope should be rendered
        Vec3d requiredOffset = entity.renderRopeOffset(tickDelta);

        if (requiredOffset != null) {
            requiredOffset = requiredOffset.multiply(-1, -1, 1);
            double length = requiredOffset.length();
            double horizAngle = atan2(requiredOffset.x, -requiredOffset.z);

            //do 50 iterations of generating quads. TODO: make this variable based on length
            int iterations = 50;
            Vec3d offsetPer = requiredOffset.multiply(1.0 / iterations);
            double offsetLen = offsetPer.length();

            Entity endingEntity = entity.getEndingEntity();

            int blockLightAtStart = getBlockLight(entity, entity.getBlockPos());
            int skyLightAtStart = entity.world.getLightLevel(LightType.SKY, entity.getBlockPos().up()); //TODO: get sky light where rope is pointing
            int blockLightAtTarget = entity.world.getLightLevel(LightType.BLOCK, entity.posAtTarget());
            int skyLightAtTarget = entity.world.getLightLevel(LightType.SKY, endingEntity.getBlockPos());

            Vec3d startingVec = Vec3d.ZERO;
            for (int i = 0; i < iterations; i++) {
                //exponential function to model hanging rope with slack
                double slackInRope = Math.max(entity.getMaxDist() / length - 1, 0);
                double slackFactor = -0.03*slackInRope*(offsetLen*i - length/2.0);
                Vec3d nextVec = startingVec.add(offsetPer).add(0, slackFactor, 0);

                int lerpBlockLight = (int) MathHelper.lerp((float)i/(float)iterations, blockLightAtStart, blockLightAtTarget);
                int lerpSkyLight = (int) MathHelper.lerp((float)i/(float)iterations, skyLightAtStart, skyLightAtTarget);
                int currLight = LightmapTextureManager.pack(lerpBlockLight, lerpSkyLight);

                Vec3d startVecLHoriz = startingVec.add(-0.05 * cos(horizAngle), 0, -0.05 * sin(horizAngle));
                Vec3d startVecRHoriz = startingVec.add(0.05 * cos(horizAngle), 0, 0.05 * sin(horizAngle));
                Vec3d endingVecLHoriz = nextVec.add(-0.05 * cos(horizAngle), 0, -0.05 * sin(horizAngle));
                Vec3d endingVecRHoriz = nextVec.add(0.05 * cos(horizAngle), 0, 0.05 * sin(horizAngle));

                Vec3d horizColor = new Vec3d(0.86, 0.67, 0.33).multiply(i % 2 == 0 ? 1.1 : 1);

                drawQuad(leashLayer, matrix, startVecLHoriz, startVecRHoriz, endingVecLHoriz, endingVecRHoriz, horizColor, currLight);

                Vec3d startVecLVert = startingVec.add(0, 0.05, 0);
                Vec3d startVecRVert = startingVec.add(0, -0.05, 0);
                Vec3d endingVecLVert = nextVec.add(0, 0.05, 0);
                Vec3d endingVecRVert = nextVec.add(0, -0.05, 0);

                Vec3d vertColor = new Vec3d(0.7, 0.52, 0.2).multiply(i % 2 == 0 ? 1.1 : 1);

                drawQuad(leashLayer, matrix, startVecLVert, startVecRVert, endingVecLVert, endingVecRVert, vertColor, currLight);

                startingVec = nextVec;
            }


        }

        matrices.pop();
    }

    private void drawQuad(VertexConsumer consumer, Matrix4f matrix, Vec3d initialL, Vec3d initialR, Vec3d endL, Vec3d endR, Vec3d color, int light) {
        consumer.vertex(matrix, (float) initialL.x, (float) initialL.y, (float) initialL.z)
                .color((float) color.x, (float) color.y, (float) color.z, 1.0f).light(light).next();
        consumer.vertex(matrix, (float) initialR.x, (float) initialR.y, (float) initialR.z)
                .color((float) color.x, (float) color.y, (float) color.z, 1.0f).light(light).next();
        consumer.vertex(matrix, (float) endR.x, (float) endR.y, (float) endR.z)
                .color((float) color.x, (float) color.y, (float) color.z, 1.0f).light(light).next();
        consumer.vertex(matrix, (float) endL.x, (float) endL.y, (float) endL.z)
                .color((float) color.x, (float) color.y, (float) color.z, 1.0f).light(light).next();
    }

}

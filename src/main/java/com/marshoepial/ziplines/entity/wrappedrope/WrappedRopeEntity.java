package com.marshoepial.ziplines.entity.wrappedrope;

import com.marshoepial.ziplines.entity.EntityRegistrar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WrappedRopeEntity extends AbstractDecorationEntity {
    private final int DISTANCE_PER_ITEM = 10;

    private static final TrackedData<Optional<UUID>> CURRENTLY_WIRING;
    private static final TrackedData<Integer> MAX_DIST;

    private ItemStack ropeStack;

    private WrappedRopeEntity wrappedToPos;

    public WrappedRopeEntity(EntityType<?> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)(Object) entityType, world);
    }

    public WrappedRopeEntity(World world, BlockPos pos) {
        super(EntityRegistrar.WRAPPED_ROPE_ENTITY_TYPE, world, pos);

        this.updatePosition(pos.getX(), pos.getY(), pos.getZ());

        this.setBoundingBox(new Box(getX() - 0.5625d, getY() - 0.1875d, getZ() - 0.5625d, getX() + 0.5625d, getY() + 0.1875d, getZ() + 0.5625d));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(CURRENTLY_WIRING, Optional.empty());
        this.dataTracker.startTracking(MAX_DIST, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient && isBeingPulled()) {
            PlayerEntity player = getCurrentlyWiring();
            double dist = player.getPos().subtract(getPos()).length();
            int maxDist = getMaxDist();
            if (dist > maxDist) {
                if (ropeStack.getCount() > 0) {
                    ropeStack.decrement(1);
                    setMaxDist(maxDist + DISTANCE_PER_ITEM);
                } else {
                    //TODO: maybe have it break instead?
                    player.setVelocity(0, 0, 0);
                }
            } else if (maxDist - dist > DISTANCE_PER_ITEM) {
                ropeStack.increment(1);
                setMaxDist(maxDist - DISTANCE_PER_ITEM);
            }
        }
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        super.updatePosition(x, y, z);
    }

    @Override
    protected void updateAttachmentPosition() {
        this.setPos(this.attachmentPos.getX()+0.5d, this.attachmentPos.getY()+0.5d, this.attachmentPos.getZ()+0.5d);
    }

    @Override
    public boolean canStayAttached() {
        return this.world.getBlockState(this.attachmentPos).getBlock().isIn(BlockTags.LOGS);
    }

    @Override
    public int getWidthPixels() {
        return 18;
    }

    @Override
    public int getHeightPixels() {
        return 6;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {

    }

    @Override
    public void onPlace() {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, EntityRegistrar.WRAPPED_ROPE_ENTITY_TYPE, 0, this.getDecorationBlockPos());
    }

    public boolean isBeingPulled() {
        return getCurrentlyWiring() != null;
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public Vec3d renderRopeOffset(float tickDelta) {
        Vec3d initialPos = getPos();
        ClientPlayerEntity cpe = (ClientPlayerEntity) getCurrentlyWiring();
        if (cpe != null) {
            //method 30951 gets the Vec3d of the players hand
            return cpe.method_30951(tickDelta).subtract(initialPos);
        } /*else if (wrappedToPos != null) {
            return wrappedTo.getModelOffset(world, wrappedToPos).subtract(initialPos);
        } TODO: implement*/
        return null;
    }

    public BlockPos posAtTarget() {
        PlayerEntity pe = getCurrentlyWiring();
        if (pe != null) {
            return pe.getBlockPos();
        } //TODO: implement attached state
        return null;
    }

    public Entity getEndingEntity() {
        if (isBeingPulled()) {
            return getCurrentlyWiring();
        }
        return null;
    }

    public int getMaxDist() {
        return this.dataTracker.get(MAX_DIST);
    }

    private void setMaxDist(int max) {
        this.dataTracker.set(MAX_DIST, max);
    }

    public PlayerEntity getCurrentlyWiring(){
        Optional<UUID> currentlyWiring = this.dataTracker.get(CURRENTLY_WIRING);
        return currentlyWiring.map(value -> this.world.getPlayerByUuid(value)).orElse(null);
    }

    private void setCurrentlyWiring(PlayerEntity player) {
        this.dataTracker.set(CURRENTLY_WIRING, Optional.ofNullable(player.getUuid()));
    }

    private void clearCurrentlyWiring() {
        this.dataTracker.set(CURRENTLY_WIRING, Optional.empty());
    }

    public void setCurrentlyWiring(PlayerEntity pulling, ItemStack ropeStack) {
        setCurrentlyWiring(pulling);
        ropeStack.decrement(1);
        this.ropeStack = ropeStack;
        setMaxDist(DISTANCE_PER_ITEM);
    }

    public void stopCurrentlyWiring() {
        clearCurrentlyWiring();
        ropeStack = null;
        setMaxDist(0);
    }

    public static WrappedRopeEntity toggleAtPos(World world, BlockPos pos) {
        if (BlockTags.LOGS.contains(world.getBlockState(pos).getBlock())) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            List<WrappedRopeEntity> list = world.getNonSpectatingEntities(WrappedRopeEntity.class, new Box((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D));

            if (list.isEmpty()) {
                WrappedRopeEntity newEntity = new WrappedRopeEntity(world, pos);
                world.spawnEntity(newEntity);
                newEntity.onPlace();
                return newEntity;
            } else {
                WrappedRopeEntity toRemove = list.get(0);
                toRemove.onBreak(null);
                toRemove.remove();
                return toRemove;
            }
        }

        return null;
    }

    /**
     * Checks the distance from the given point to the rope.
     * This does not use the precise rendering-only methods.
     * @param point The point at which to check from
     * @return The distance from the point to the nearest point on the rope. If there is no rope, returns the distance to the entity.
     */
    public double distFromRope(Vec3d point) {
        return point.distanceTo(getClosestPointToRope(point));
    }

    public Vec3d getClosestPointToRope(Vec3d point) {
        if (isBeingPulled()) {
            //https://stackoverflow.com/questions/6068660/checking-a-line-segment-is-within-a-distance-from-a-point
            Vec3d wiringFrom = getCurrentlyWiring().getPos();

            Vec3d v = wiringFrom.subtract(getPos());
            Vec3d w = point.subtract(getPos());

            double c1 = w.dotProduct(v);
            if (c1 <= 0){
                return getPos();
            }
            double c2 = v.dotProduct(v);
            if (c2 <= c1) {
                return wiringFrom;
            }

            return getPos().add(v.multiply(c1 / c2));

        } else {
            return getPos();
        }
    }

    static {
        CURRENTLY_WIRING = DataTracker.registerData(WrappedRopeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        MAX_DIST = DataTracker.registerData(WrappedRopeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}

package com.marshoepial.ziplines.items;

import com.marshoepial.ziplines.entity.wrappedrope.WrappedRopeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RopeItem extends Item {
    public RopeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        BlockPos hitPos = context.getBlockPos();
        WrappedRopeEntity entity = WrappedRopeEntity.toggleAtPos(world, hitPos);
        if (entity != null) {
            if (!entity.removed) {
                entity.setCurrentlyWiring(context.getPlayer(), context.getStack());
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }


}

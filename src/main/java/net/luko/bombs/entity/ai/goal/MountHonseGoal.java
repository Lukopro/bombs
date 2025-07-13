package net.luko.bombs.entity.ai.goal;

import net.luko.bombs.entity.HonseEntity;
import net.luko.bombs.entity.ProspectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;

public class MountHonseGoal extends Goal {
    private final ProspectorEntity prospector;
    private final float remountRange;
    private final float dismountRange;
    private HonseEntity targetHonse;
    private LivingEntity target;

    public MountHonseGoal(ProspectorEntity prospector, float remountRange, float dismountRange){
        this.prospector = prospector;
        this.remountRange = remountRange;
        this.dismountRange = dismountRange;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse(){
        if(prospector.isPassenger()) return false;

        target = prospector.getTarget();
        if(target == null || !target.isAlive() || prospector.distanceTo(target) < remountRange) return false;

        Level level = prospector.level();
        AABB box = prospector.getBoundingBox().inflate(48.0);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        if(!level.hasChunksAt(min, max)) return false;

        targetHonse = level.getEntitiesOfClass(
                HonseEntity.class, box
                ).stream()
                .filter(honse -> level.isLoaded(honse.blockPosition()))
                .filter(honse -> (honse.getPassengers().size() < 2 && !honse.isTamed()))
                .min(Comparator.comparing(prospector::distanceTo))
                .orElse(null);

        return targetHonse != null && !targetHonse.isTamed();
    }

    @Override
    public boolean canContinueToUse(){
        return !prospector.isPassenger() && targetHonse != null && targetHonse.isAlive() && targetHonse.getPassengers().size() < 2 && prospector.distanceTo(target) > dismountRange;
    }

    @Override
    public void tick(){
        if(targetHonse == null || prospector.isPassenger() || !targetHonse.isAlive()) return;

        if(prospector.distanceTo(targetHonse) > 2.0){
            prospector.getNavigation().moveTo(targetHonse, 1.0F);
        } else {
            prospector.startRiding(targetHonse, true);

            if(target != null && target.isAlive()){
                prospector.setTarget(target);
            }
        }
    }

    @Override
    public void stop(){
        targetHonse = null;
    }
}

package net.luko.bombs.entity.ai.goal;

import net.luko.bombs.entity.HonseEntity;
import net.luko.bombs.entity.ProspectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;

public class FollowProspectorGoal extends Goal {
    private final HonseEntity honse;
    private ProspectorEntity targetProspector;
    private final double speed;
    private final float maxDist;
    private final float startDist;
    private final float stopDist;

    public FollowProspectorGoal(HonseEntity honse, double speed, float maxDist, float startDist, float stopDist){
        this.honse = honse;
        this.speed = speed;
        this.maxDist = maxDist;
        this.startDist = startDist;
        this.stopDist = stopDist;
    }

    @Override
    public boolean canUse(){
        if(honse.isVehicle()) return false;

        Level level = honse.level();
        AABB box = honse.getBoundingBox().inflate(maxDist);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        if(!level.hasChunksAt(min, max)) return false;


        targetProspector = honse.level().getEntitiesOfClass(
                ProspectorEntity.class,
                honse.getBoundingBox().inflate(maxDist))
                .stream()
                .filter(prospector -> level.isLoaded(prospector.blockPosition()))
                .filter(prospector -> !prospector.isPassenger())
                .min(Comparator.comparing(prospector -> prospector.distanceToSqr(honse)))
                .orElse(null);

        return targetProspector != null && targetProspector.isAlive() && honse.distanceTo(targetProspector) > startDist;
    }

    @Override
    public boolean canContinueToUse(){
        return targetProspector != null
                && !honse.isVehicle()
                && targetProspector.isAlive()
                && honse.distanceTo(targetProspector) > stopDist
                && honse.distanceTo(targetProspector) < maxDist;
    }

    @Override
    public void tick(){
        if(canContinueToUse()){
            honse.getNavigation().moveTo(targetProspector, speed);
        }
    }

    @Override
    public void stop(){
        honse.getNavigation().stop();
    }
}

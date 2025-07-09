package net.luko.bombs.entity.ai.goal;

import net.luko.bombs.entity.HonseEntity;
import net.luko.bombs.entity.ProspectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PursueWhileMountedGoal extends Goal {
    private final ProspectorEntity prospector;
    private final float dismountRange;
    private LivingEntity target;

    public PursueWhileMountedGoal(ProspectorEntity prospector, float dismountRange){
        this.prospector = prospector;
        this.dismountRange = dismountRange;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse(){
        if(!(prospector.getVehicle() instanceof HonseEntity)) return false;

        target = prospector.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse(){
        return prospector.getVehicle() instanceof HonseEntity
                && target != null && target.isAlive()
                && prospector.distanceTo(target) > dismountRange;
    }

    @Override
    public void tick(){
        if(target == null || !(prospector.getVehicle() instanceof HonseEntity honse)) return;

        boolean waitForOtherProspector = honse.getPassengers().size() < 2 &&
                prospector.level().getEntitiesOfClass(
                ProspectorEntity.class,
                honse.getBoundingBox().inflate(12.0))
                .stream().anyMatch(other ->
                        other != prospector &&
                        !other.isPassenger() &&
                        other.getNavigation().getTargetPos() != null &&
                        other.getNavigation().getTargetPos().closerThan(BlockPos.containing(honse.position()), 6.0) &&
                        other.getTarget() == prospector.getTarget()
        );

        if(waitForOtherProspector){
            prospector.getNavigation().stop();
            return;
        }

        prospector.getNavigation().moveTo(target, 1.5);
    }

    @Override
    public void stop(){
        prospector.stopRiding();
    }
}

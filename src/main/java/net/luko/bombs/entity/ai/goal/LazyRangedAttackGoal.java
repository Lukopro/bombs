package net.luko.bombs.entity.ai.goal;

import net.luko.bombs.entity.ProspectorEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

public class LazyRangedAttackGoal extends RangedAttackGoal {
    private final ProspectorEntity prospector;
    private final float disengageRadius;

    public LazyRangedAttackGoal(ProspectorEntity prospector, double speedModifier, int attackInterval, float attackRadius, float disengageRadius) {
        super(prospector, speedModifier, attackInterval, attackRadius);
        this.prospector = prospector;
        this.disengageRadius = disengageRadius;
    }

    @Override
    public boolean canUse(){
        return prospector.getTarget() != null
                && this.prospector.distanceTo(prospector.getTarget()) < disengageRadius
                && super.canUse();
    }

    @Override
    public boolean canContinueToUse(){
        return prospector.getTarget() != null
                && this.prospector.distanceTo(prospector.getTarget()) < disengageRadius
                && super.canContinueToUse();
    }

    @Override
    public void stop(){

    }
}

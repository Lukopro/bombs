package net.luko.bombs.entity.bomb;

import net.luko.bombs.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThrownGrenadeEntity extends ThrownBombEntity{
    private int fuseTime;

    public float lastTick = 0;
    public float lastSpin = 0;

    public ThrownGrenadeEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        super(type, level);
        this.fuseTime = getFuseTime();
        this.noPhysics = false;
    }

    public ThrownGrenadeEntity(EntityType<? extends ThrownBombEntity> type, Level level, float explosionPower) {
        super(type, level, explosionPower);
        this.fuseTime = getFuseTime();
        this.noPhysics = false;
    }

    public ThrownGrenadeEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower) {
        super(type, level, thrower, explosionPower);
        this.fuseTime = getFuseTime();
        this.noPhysics = false;
    }

    @Override
    protected Item getDefaultItem(){
        return ModItems.GRENADE.get();
    }

    private int getFuseTime(){
        return 100;
    }

    public float getBaseGravity(){
        return 0.05F;
    }

    @Override
    public boolean isPushable(){
        return tickCount >= 2;
    }

    @Override
    public boolean canBeCollidedWith(){
        return tickCount >= 2;
    }

    @Override
    public void tick(){
        if(tickCount >= fuseTime) this.explode();
        this.updateYRot();

        Vec3 beforeMove = this.position();
        Vec3 preVelocity = this.getDeltaMovement();

        this.move(MoverType.SELF, preVelocity);

        Vec3 afterMove = this.position();
        Vec3 postVelocity = this.getDeltaMovement();

        if(!beforeMove.equals(afterMove) || !preVelocity.equals(postVelocity)) {
            checkBounce(preVelocity, postVelocity);
        }

        this.setDeltaMovement(this.getDeltaMovement().add(0F, -this.getGravity(), 0F));
    }

    private void updateYRot(){
        Vec3 motion = this.getDeltaMovement();
        if(motion.lengthSqr() > 0.1){
            this.setYRot((float) (Mth.atan2(-motion.z, motion.x) * (180F / Math.PI)) - 90F);
        }
    }

    private void checkBounce(Vec3 pre, Vec3 post){
        if(pre.x != post.x && post.x == 0)
            applyBounce(Direction.Axis.X, pre);

        if(pre.y != post.y && post.y == 0 && Math.abs(pre.y) >= this.getGravity())
            applyBounce(Direction.Axis.Y, pre);

        if(pre.z != post.z && post.z == 0)
            applyBounce(Direction.Axis.Z, pre);
    }

    private void applyBounce(Direction.Axis axis, Vec3 previousVelocity){
        double bounceFactor = 0.6;
        double frictionFactor = 0.8;

        switch (axis){
            case X -> setDeltaMovement(-previousVelocity.x * bounceFactor, previousVelocity.y * frictionFactor, previousVelocity.z * frictionFactor);
            case Y -> setDeltaMovement(previousVelocity.x * frictionFactor, -previousVelocity.y * bounceFactor, previousVelocity.z * frictionFactor);
            case Z -> setDeltaMovement(previousVelocity.x * frictionFactor, previousVelocity.y * frictionFactor, -previousVelocity.z * bounceFactor);
        }
    }
}

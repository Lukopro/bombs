package net.luko.bombs.entity;

import net.luko.bombs.entity.ai.goal.FollowProspectorGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HonseEntity extends AbstractHorse {
    private float jumpPower = 0.0F;

    protected HonseEntity(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setFlag(4, true);
    }

    @Override
    public boolean canAddPassenger(Entity passenger){
        return getPassengers().size() < 2;
    }

    @Override
    public Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor){
        return new Vec3(0.0, 1.8, 0.0);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Mob mob) {
            return mob;
        } else if (entity instanceof Player player) {
            return player;
        }

        return null;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunc) {
        super.positionRider(passenger, moveFunc);

        int index = this.getPassengers().indexOf(passenger);
        if(index < 0) return;

        float spacing = 0.6F;
        float yawRad = this.getYRot() * ((float)Math.PI / 180.0F);
        float offsetX = Mth.sin(yawRad) * index * spacing;
        float offsetZ = -Mth.cos(yawRad) * index * spacing;

        moveFunc.accept(
                passenger,
                this.getX() + offsetX,
                this.getY() + this.getPassengerAttachmentPoint(passenger, passenger.getDimensions(passenger.getPose()), 1.0F).y - (passenger.getBbHeight() / 2.0),
                this.getZ() + offsetZ
        );

        if(passenger instanceof LivingEntity living){
            living.yBodyRot = this.yBodyRot;
        }
    }

    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(1, new FollowProspectorGoal(this, 1.0, 64.0F, 32.0F, 24.0F));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
    }

    @Override
    public boolean isPushable(){
        return true;
    }

    @Override
    public boolean isSaddled(){
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source){
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound(){
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand){
        if(!this.isTamed()){
            this.setOwnerUUID(player.getUUID());
            this.setTamed(true);
            this.level().broadcastEntityEvent(this, (byte)7);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if(this.isTamed() && !this.isVehicle()){
            player.startRiding(this);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.JUMP_STRENGTH, 0.8);
    }
}

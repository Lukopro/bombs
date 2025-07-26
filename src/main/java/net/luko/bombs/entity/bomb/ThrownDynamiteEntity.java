package net.luko.bombs.entity.bomb;

import net.luko.bombs.item.ModItems;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownDynamiteEntity extends ThrownBombEntity{
    public ThrownDynamiteEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        super(type, level);
    }

    public ThrownDynamiteEntity(EntityType<? extends ThrownBombEntity> type, Level level, float explosionPower) {
        super(type, level, explosionPower);
    }

    public ThrownDynamiteEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower) {
        super(type, level, thrower, explosionPower);
    }

    @Override
    protected Item getDefaultItem(){
        return ModItems.DYNAMITE.get();
    }

    @Override
    public float getBaseGravity() {
        return 0.03F;
    }

    protected boolean hasHydrosensitiveModifier(){
        if(hasHydrosensitiveModifier == null){
            hasHydrosensitiveModifier = BombModifierUtil.hasModifier(getItem(), "hydrosensitive");
        }
        return hasHydrosensitiveModifier;
    }

    @Override
    public void tick(){
        super.tick();
        if(!level().isClientSide() && this.isInWaterOrBubble() && hasHydrosensitiveModifier()) this.explode();
    }

    @Override
    protected void onHitBlock(BlockHitResult result){
        if(!level().isClientSide()){
            this.explode();
        }
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result){
        if(result.getEntity() instanceof ThrownBombEntity) return;
        if(!level().isClientSide()){
            this.explode();
        }
        super.onHitEntity(result);
    }
}
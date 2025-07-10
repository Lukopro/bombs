package net.luko.bombs.entity;

import net.luko.bombs.entity.ai.goal.LazyRangedAttackGoal;
import net.luko.bombs.entity.ai.goal.MountHonseGoal;
import net.luko.bombs.entity.ai.goal.PursueWhileMountedGoal;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModItems;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ProspectorEntity extends PathfinderMob implements RangedAttackMob {
    private static final float ATTACK_RANGE = 8.0F;
    private static final float DISMOUNT_RANGE = 16.0F;
    private static final float REMOUNT_RANGE = 24.0F;

    protected ProspectorEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(1, new PursueWhileMountedGoal(this, DISMOUNT_RANGE));
        this.goalSelector.addGoal(2, new MountHonseGoal(this, REMOUNT_RANGE, DISMOUNT_RANGE));
        this.goalSelector.addGoal(3, new LazyRangedAttackGoal(this, 1.0, 40, ATTACK_RANGE, REMOUNT_RANGE));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                entity -> entity instanceof Player player && !player.isCreative() && !player.isSpectator()));
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source){
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound(){
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitVec, InteractionHand hand){
        ItemStack heldStack = player.getItemInHand(hand);

        if(!heldStack.isEmpty() && heldStack.getItem() instanceof BombItem){
            if(this.getMainHandItem().isEmpty()){
                this.setItemInHand(InteractionHand.MAIN_HAND, heldStack.copy());
                heldStack.setCount(0);
            } else {
                return InteractionResult.FAIL;
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.interactAt(player, hitVec, hand);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 128.0);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ItemStack stack = this.getMainHandItem();
        if(stack.getItem() instanceof BombItem bombItem) {
            this.lookControl.setLookAt(target, 30.0F, 30.0F);

            Vec3 throwerPos = this.position().add(0, this.getEyeHeight(), 0);
            Vec3 targetPos = target.position().add(0, target.getEyeHeight() / 2.0, 0);

            Vec3 diff = targetPos.subtract(throwerPos);

            double horizontalDistance = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
            float pitch = (float)-(Mth.atan2(diff.y, horizontalDistance) * (180.0F / Math.PI));

            bombItem.throwBomb(this.level(), this, pitch, this.getYHeadRot(), stack.copy(), bombItem.getBaseVelocity(stack));
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer){
        return false;
    }

    public void giveSpawnItems(){
        ItemStack chest = new ItemStack(Items.LEATHER_CHESTPLATE);
        chest.enchant(Enchantments.BLAST_PROTECTION, 10);
        chest.addTagElement("HideFlags", IntTag.valueOf(ItemStack.TooltipPart.ENCHANTMENTS.getMask()));
        this.setItemSlot(EquipmentSlot.CHEST, chest);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);

        ItemStack bomb = new ItemStack(ModItems.DYNAMITE.get());
        bomb.getOrCreateTag().putInt("Tier", 2);
        ListTag modifierTag = new ListTag();
        modifierTag.add(StringTag.valueOf("contained"));
        bomb.getTag().put("Modifiers", modifierTag);
        this.setItemInHand(InteractionHand.MAIN_HAND, bomb);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
}

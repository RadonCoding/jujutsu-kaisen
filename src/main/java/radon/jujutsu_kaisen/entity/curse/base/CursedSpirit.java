package radon.jujutsu_kaisen.entity.curse.base;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class CursedSpirit extends SummonEntity implements GeoEntity, ISorcerer, ICommandable {
    private static final double AWAKEN_RANGE = 8.0D;
    private static final double HUNGRY_AWAKEN_RANGE = 8.0D;
    private static final int HUNGRY_CHANCE = 300;
    private static final int UPDATE_INTERVAL = 5 * 20;

    private boolean hungry;

    private static final EntityDataAccessor<Boolean> DATA_HIDING = SynchedEntityData.defineId(CursedSpirit.class, EntityDataSerializers.BOOLEAN);

    protected CursedSpirit(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.setTame(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_HIDING, false);
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    public void setHiding(boolean hiding) {
        this.entityData.set(DATA_HIDING, hiding);
    }

    @Override
    public boolean isPersistenceRequired() {
        return this.getGrade().ordinal() > SorcererGrade.GRADE_1.ordinal();
    }

    protected abstract boolean isCustom();

    protected boolean canFly() { return false; }

    protected boolean targetsCurses() { return false; }

    protected boolean targetsSorcerers() { return true; }

    private void createGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));

        if (this.hasMeleeAttack()) {
            this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        }
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, this.canFly()));

            this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
        } else {
            this.targetSelector.addGoal(target++, new HurtByTargetGoal(this, CursedSpirit.class));
            this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
            this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));

            if (this.targetsSorcerers()) {
                this.targetSelector.addGoal(target++, new NearestAttackableSorcererGoal(this, true));
            }
            if (this.targetsCurses()) {
                this.targetSelector.addGoal(target, new NearestAttackableCurseGoal(this, true));
            }
        }
        this.goalSelector.addGoal(goal++, new ChantGoal<>(this));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (!super.canAttack(pTarget)) return false;

        if (!this.isTame()) return true;

        if (pTarget == this.getOwner()) return false;

        if (!(pTarget instanceof TamableAnimal)) return true;

        while (pTarget instanceof TamableAnimal tamable1) {
            if (!(tamable1.getOwner() instanceof TamableAnimal tamable2)) break;

            pTarget = tamable2;
        }
        return ((TamableAnimal) pTarget).getOwner() != this.getOwner() || ((TamableAnimal) pTarget).isTame() != this.isTame();
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);

        if (!this.isTame() && pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker) && attacker != this) {
            this.setTarget(attacker);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isNoAi() {
        return super.isNoAi() || this.isHiding();
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return this.isHiding() || super.isInvulnerableTo(pSource);
    }

    @Override
    public boolean isPickable() {
        return !this.isHiding() && super.isPickable();
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return !this.isHiding() && super.shouldRender(pX, pY, pZ);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isHiding() && super.canBeSeenAsEnemy();
    }

    private void update() {
        if (this.getGrade().ordinal() == SorcererGrade.SPECIAL_GRADE.ordinal()) return;

        if (this.getTime() % UPDATE_INTERVAL != 0) return;

        if (this.random.nextInt(HUNGRY_CHANCE) == 0) {
            this.hungry = !this.hungry;
        }

        this.setHiding(this.getTarget() == null && !VeilHandler.isProtectedByVeil(((ServerLevel) this.level()), this.blockPosition()));

        if (!this.isHiding()) return;

        double range = this.hungry ? HUNGRY_AWAKEN_RANGE : AWAKEN_RANGE;

        TargetingConditions conditions = TargetingConditions.forCombat().range(range)
                .selector(entity -> {
                    if (entity instanceof AbstractVillager) return true;

                    if (entity instanceof Player) {
                        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

                        if (cap == null) return true;

                        ISorcererData data = cap.getSorcererData();

                        return data.getType() == JujutsuType.SORCERER;
                    }
                    return false;
                });

        AABB bounds = this.getBoundingBox().inflate(range, 4.0D, range);

        LivingEntity target = this.level()
                .getNearestEntity(
                        this.level().getEntitiesOfClass(LivingEntity.class, bounds),
                        conditions,
                        this,
                        this.getX(),
                        this.getEyeY(),
                        this.getZ()
                );

        if (target != null) {
            this.setTarget(target);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isTame()) {
            LivingEntity target = this.getTarget();
            this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());
            return;
        }

        if (!this.level().isClientSide) {
        this.update();
        }
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.CURSE;
    }

    @Override
    public boolean canChangeTarget() {
        return this.isTame();
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.isCustom()) this.createGoals();

        if (!this.level().isClientSide) {
            this.update();
        }
    }

    @Override
    public Summon<?> getAbility() {
        return null;
    }
}

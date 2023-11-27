package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.WaterWalkingFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableHumanGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Attack non-sorcerers
public class SuguruGetoEntity extends SorcererEntity {
    public SuguruGetoEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.CURSE_MANIPULATION;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    public void init(ISorcererData data) {
        super.init(data);

        Collection<RegistryObject<EntityType<?>>> registry = JJKEntities.ENTITIES.getEntries();

        for (RegistryObject<EntityType<?>> entry : registry) {
            EntityType<?> type = entry.get();

            if (type.create(this.level()) instanceof ISorcerer sorcerer && sorcerer.getJujutsuType() == JujutsuType.CURSE && sorcerer.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal()) {
                for (int i = 0; i < SorcererGrade.values().length - sorcerer.getGrade().ordinal(); i++) {
                    data.addCurse(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type);
                }
            }
        }
        data.addCurse(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.KUCHISAKE_ONNA.get());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JJKItems.SUGURU_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(JJKItems.SUGURU_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(JJKItems.SUGURU_BOOTS.get()));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        LivingEntity target = this.getTarget();

        if (target != null && this.random.nextInt(20) == 0) {
            Registry<EntityType<?>> registry = this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            List<EntityType<?>> curses = new ArrayList<>(cap.getCurses(registry).keySet());

            if (!curses.isEmpty()) {
                EntityType<?> curse = curses.get(this.random.nextInt(curses.size()));
                JJKAbilities.summonCurse(this, curse, Math.min(1, this.random.nextInt(cap.getCurseCount(registry, curse))));
            }
        }

        ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);

        if (stack.is(JJKItems.CURSED_SPIRIT_ORB.get())) {
            this.playSound(this.getEatingSound(stack), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

            Registry<EntityType<?>> registry = this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            cap.addCurse(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), CursedSpiritOrbItem.getCurse(registry, stack));
            this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 1));
        }
    }

    @Override
    protected boolean isCustom() {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterWalkingFloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new SorcererGoal(this));
        this.goalSelector.addGoal(4, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableHumanGoal(this, false));
    }
}

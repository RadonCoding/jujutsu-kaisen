package radon.jujutsu_kaisen.ability.disaster_plants;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.WoodShieldEntity;

import java.util.List;

public class WoodShield extends Summon<WoodShieldEntity> {
    public WoodShield() {
        super(WoodShieldEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.getHealth() / owner.getMaxHealth() < 0.25F;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.WOOD_SHIELD_SEGMENT.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected WoodShieldEntity summon(LivingEntity owner) {
        return new WoodShieldEntity(owner);
    }

    @Override
    protected boolean shouldRemove() {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 2.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            if (!abilityData.hasToggled(JJKAbilities.WOOD_SHIELD.get())) return;

            WoodShieldEntity shield = sorcererData.getSummonByClass(WoodShieldEntity.class);

            if (shield != null) {
                shield.hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }
}

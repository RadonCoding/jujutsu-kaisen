package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
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
    public boolean display() {
        return false;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class WoodShieldForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide || !JJKAbilities.hasToggled(victim, JJKAbilities.WOOD_SHIELD.get()))
                return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            WoodShieldEntity shield = cap.getSummonByClass(WoodShieldEntity.class);

            if (shield != null) {
                shield.hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }
}

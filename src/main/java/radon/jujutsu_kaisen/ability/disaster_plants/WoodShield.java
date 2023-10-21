package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.WoodShieldEntity;

import java.util.List;

public class WoodShield extends Summon<WoodShieldEntity> {
    public WoodShield() {
        super(WoodShieldEntity.class);
    }

    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return !owner.level().getEntities(owner, owner.getBoundingBox().inflate(3.0D),
                entity -> entity instanceof Projectile projectile && projectile.getOwner() != owner && projectile.getDeltaMovement().lengthSqr() >= 1.0E-7D).isEmpty();
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.WOOD_SHIELD.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected WoodShieldEntity summon(int index, LivingEntity owner) {
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
    public boolean isTechnique() {
        return true;
    }

    @Override
    public boolean display() {
        return false;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (JJKAbilities.hasToggled(victim, JJKAbilities.WOOD_SHIELD.get())) {
                event.setAmount(event.getAmount() * 0.5F);
            }
        }
    }
}

package radon.jujutsu_kaisen.ability.cursed_speech;


import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.cursed_speech.util.CursedSpeechUtil;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Explode extends CursedSpeech {
    private static final float EXPLOSIVE_POWER = 1.5F;
    private static final float MAX_EXPLOSIVE_POWER = 20.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;
        return CursedSpeechUtil.getTargets(owner).contains(target) && (owner.getHealth() / owner.getMaxHealth() <= 0.5F || HelperMethods.RANDOM.nextInt(10) == 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            ExplosionHandler.spawn(owner.level().dimension(), entity.position().add(0.0D, entity.getBbHeight() / 2, 0.0D), Math.min(MAX_EXPLOSIVE_POWER, EXPLOSIVE_POWER * this.getOutput(owner)),
                    20, this.getOutput(owner) * 0.25F, owner, JJKDamageSources.jujutsuAttack(owner, this), false);

            if (entity instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.explode", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        });
    }

    @Override
    public int getThroatDamage() {
        return 3 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 300.0F;
    }

    @Override
    public int getCooldown() {
        return 20 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}

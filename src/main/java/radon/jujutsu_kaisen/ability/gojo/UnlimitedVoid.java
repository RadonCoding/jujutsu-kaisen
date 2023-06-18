package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.UnlimitedVoidS2CPacket;

public class UnlimitedVoid extends DomainExpansion {
    private static final int RADIUS = 20;
    private static final int DURATION = 30 * 20;

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    protected int getRadius() {
        return RADIUS;
    }

    @Override
    protected int getDuration() {
        return DURATION;
    }

    @Override
    protected DomainBlock getBlock() {
        return JJKBlocks.INFINITE_VOID.get();
    }

    @Override
    public void onHit(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), DURATION, 0, false, false, false));
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, DURATION, 4));

            if (living instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new UnlimitedVoidS2CPacket(DURATION), player);
            }
        }
    }

    @Override
    public int getCooldown() {
        return 60 * 20;
    }
}

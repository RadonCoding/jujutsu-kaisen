package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.block.JujutsuBlocks;
import radon.jujutsu_kaisen.effect.JujutsuEffects;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.UnlimitedVoidS2CPacket;

public class UnlimitedVoid extends DomainExpansion {
    private static final int RADIUS = 15;
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
        return JujutsuBlocks.INFINITE_VOID.get();
    }

    @Override
    protected void onHit(LivingEntity owner, Entity entity) {
        if (!owner.level.isClientSide) {
            PacketHandler.sendToClient(new UnlimitedVoidS2CPacket(DURATION), (ServerPlayer) owner);
        }

        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(JujutsuEffects.STUN.get(), DURATION));

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

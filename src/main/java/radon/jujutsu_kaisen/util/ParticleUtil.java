package radon.jujutsu_kaisen.util;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ParticleUtil {

    private static void sendParticles(ServerLevel pLevel, ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        if (pPlayer.level() == pLevel) {
            BlockPos pos = pPlayer.blockPosition();

            if (pos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), pLongDistance ? 512.0D : 32.0D)) {
                pPlayer.connection.send(pPacket);
            }
        }
    }

    public static <T extends ParticleOptions> void sendParticle(ServerPlayer player, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, (float) pXSpeed, (float) pYSpeed, (float) pZSpeed, 1.0F, 0);
        sendParticles(player.serverLevel(), player, pLongDistance, pPosX, pPosY, pPosZ, packet);
    }

    public static <T extends ParticleOptions> void sendParticles(ServerLevel pLevel, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, (float) pXSpeed, (float) pYSpeed, (float) pZSpeed, 1.0F, 0);

        for (int i = 0; i < pLevel.players().size(); i++) {
            ServerPlayer player = pLevel.players().get(i);
            sendParticles(pLevel, player, pLongDistance, pPosX, pPosY, pPosZ, packet);
        }
    }
}

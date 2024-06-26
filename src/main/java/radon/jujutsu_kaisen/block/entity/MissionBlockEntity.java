package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.OpenMissionScreenS2CPacket;

import java.util.*;

public class MissionBlockEntity extends BlockEntity {
    protected static final AABB TOUCH_AABB = new AABB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.25D, 0.9375D);

    private final Set<UUID> active;

    public MissionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.MISSION.get(), pPos, pBlockState);

        this.active = new HashSet<>();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MissionBlockEntity pBlockEntity) {
        List<Player> players = pLevel.getEntitiesOfClass(Player.class, TOUCH_AABB.move(pPos.above()), EntitySelector.NO_SPECTATORS.and(entity -> !entity.isIgnoringBlockTriggers()));

        for (Player player : players) {
            if (pBlockEntity.active.contains(player.getUUID())) continue;

            PacketDistributor.sendToPlayer((ServerPlayer) player, OpenMissionScreenS2CPacket.INSTANCE);
            pBlockEntity.active.add(player.getUUID());
        }

        Iterator<UUID> iter = pBlockEntity.active.iterator();

        while (iter.hasNext()) {
            UUID identifier = iter.next();

            if (!(((ServerLevel) pLevel).getEntity(identifier) instanceof Player player)) {
                iter.remove();
                continue;
            }

            if (!players.contains(player)) {
                iter.remove();
            }
        }
    }
}

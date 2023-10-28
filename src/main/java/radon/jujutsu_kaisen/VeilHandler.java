package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {
    private static final Map<ResourceKey<Level>, BlockPos> veils = new HashMap<>();

    public static void create(ResourceKey<Level> dimension, BlockPos pos) {
        veils.put(dimension, pos);
    }

    public static boolean canSpawn(Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, BlockPos> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();
            BlockPos pos = entry.getValue();

            if (mob.level().dimension() != dimension || !(mob.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be)) continue;

            int radius = be.getSize();
            BlockPos relative = target.subtract(pos);

            if (relative.distSqr(Vec3i.ZERO) < radius  * radius) {
                return false; //VeilBlockEntity.isAllowed(pos, mob);
            }
        }
        return true;
    }

    public static boolean isProtected(Level accessor, BlockPos target) {
        for (Map.Entry<ResourceKey<Level>, BlockPos> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();
            BlockPos pos = entry.getValue();

            if (accessor.dimension() != dimension || !(accessor.getBlockEntity(pos) instanceof VeilRodBlockEntity be)) continue;

            int radius = be.getSize();
            BlockPos relative = target.subtract(pos);

            if (relative.distSqr(Vec3i.ZERO) < radius  * radius) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.type != TickEvent.Type.LEVEL || event.phase == TickEvent.Phase.START || event.level.isClientSide) return;

        veils.entrySet().removeIf(entry ->
                event.level.dimension() == entry.getKey() && !(event.level.getBlockEntity(entry.getValue()) instanceof VeilRodBlockEntity));
    }
}

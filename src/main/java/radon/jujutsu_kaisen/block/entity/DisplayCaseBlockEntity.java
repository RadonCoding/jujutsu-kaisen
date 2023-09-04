package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DisplayCaseBlockEntity extends BlockEntity {
    private static final int RARITY = 50;
    private static final double SPAWN_RADIUS = 64.0D;
    private static final int INTERVAL = 5;

    private ItemStack stack = ItemStack.EMPTY;
    private int rot = 0;

    public DisplayCaseBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DISPLAY_CASE.get(), pPos, pBlockState);
    }

    private static @Nullable Entity getRandomCurse(Level level, float energy) {
        List<Entity> pool = new ArrayList<>();

        Collection<RegistryObject<EntityType<?>>> registry = JJKEntities.ENTITIES.getEntries();

        for (RegistryObject<EntityType<?>> entry : registry) {
            EntityType<?> type = entry.get();

            if (type.is(JJKEntityTypeTags.SPAWNABLE_CURSE) && type.create(level) instanceof LivingEntity entity && entity instanceof ISorcerer sorcerer && sorcerer.getGrade().getPower(entity) <= energy) {
                pool.add(entity);
            }
        }
        return pool.isEmpty() ? null : pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
    }

    public float getEnergy() {
        if (this.stack.getItem() instanceof CursedObjectItem obj) {
            int index = Mth.clamp(obj.getGrade().ordinal() - 1, 0, SorcererGrade.values().length - 1);
            return SorcererGrade.values()[index].getPower();
        }
        return 0.0F;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DisplayCaseBlockEntity pBlockEntity) {
        pBlockEntity.rotTick();

        if (pLevel.getGameTime() % INTERVAL != 0 || pLevel.isClientSide) return;

        AtomicReference<Float> energy = new AtomicReference<>(pBlockEntity.getEnergy());

        int centerX = pPos.getX() >> 4;
        int centerZ = pPos.getZ() >> 4;

        for (int x = centerX - 1; x <= centerX + 1; x++) {
            for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                ChunkAccess chunk = pLevel.getChunk(x, z);

                for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                    if (pos.equals(pPos)) continue;

                    BlockState state = pLevel.getBlockState(pos);

                    if (!state.is(JJKBlocks.DISPLAY_CASE.get())) continue;

                    if (pLevel.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be) {
                        energy.set(energy.get() + be.getEnergy());
                    }
                }
            }
        }

        Entity curse = getRandomCurse(pLevel, energy.get());

        if (curse == null) return;

        int rng = Mth.floor((energy.get() * RARITY)) / (pLevel.isNight() ? 2 : 1);

        if (HelperMethods.RANDOM.nextInt(rng) == 0) {
            double d0 = (double) pPos.getX() + (HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) * SPAWN_RADIUS + 0.5D;
            double d1 = pPos.getY() + HelperMethods.RANDOM.nextInt(10) - 1;
            double d2 = (double) pPos.getZ() + (HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) * SPAWN_RADIUS + 0.5D;

            EntityType<?> type = curse.getType();

            if (pLevel.noCollision(type.getAABB(d0, d1, d2))) {
                if (!type.getCategory().isFriendly() && pLevel.getDifficulty() == Difficulty.PEACEFUL) {
                    return;
                }
                curse.setPos(d0, d1, d2);
                curse.moveTo(curse.getX(), curse.getY(), curse.getZ(), HelperMethods.RANDOM.nextFloat() * 360.0F, 0.0F);

                if (curse instanceof Mob mob) {
                    if (!mob.checkSpawnRules(pLevel, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(pLevel)) {
                        return;
                    }
                    ForgeEventFactory.onFinalizeSpawn(mob, (ServerLevel) pLevel, pLevel.getCurrentDifficultyAt(curse.blockPosition()), MobSpawnType.SPAWNER, null, null);
                }
                if (!pLevel.addFreshEntity(curse)) return;
                if (curse instanceof Mob mob) mob.spawnAnim();
            }
        }
    }

    public void rotTick() {
        if (this.rot != Short.MIN_VALUE) {
            this.rot++;
        }
    }

    public int getRot() {
        return this.rot;
    }

    public ItemStack getItem() {
        return this.stack;
    }

    public void setItem(ItemStack stack) {
        this.stack = stack;
        this.stack.setCount(1);

        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putInt("rotation", this.rot);
        pTag.put("stack", this.stack.save(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.rot = pTag.getInt("rotation");
        this.stack = ItemStack.of(pTag.getCompound("stack"));
    }
}

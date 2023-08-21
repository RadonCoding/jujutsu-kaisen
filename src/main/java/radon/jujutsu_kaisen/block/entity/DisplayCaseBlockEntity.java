package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayCaseBlockEntity extends BlockEntity {
    private static final int RARITY = 500;
    private static final int RADIUS = 8;
    private static final double SPAWN_RADIUS = 128.0D;

    private ItemStack stack = ItemStack.EMPTY;
    private int rot = 0;

    public DisplayCaseBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DISPLAY_CASE.get(), pPos, pBlockState);
    }

    private static @Nullable EntityType<?> getRandomCurse(Level level, float energy) {
        List<EntityType<?>> pool = new ArrayList<>();

        Collection<RegistryObject<EntityType<?>>> registry = JJKEntities.ENTITIES.getEntries();

        for (RegistryObject<EntityType<?>> entry : registry) {
            EntityType<?> type = entry.get();

            if (type.is(JJKEntityTypeTags.SPAWNABLE_CURSE) && type.create(level) instanceof ISorcerer sorcerer && sorcerer.getGrade().getPower() < energy) {
                pool.add(type);
            }
        }
        return pool.isEmpty() ? null : pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
    }

    public int getEnergy() {
        if (this.stack.getItem() instanceof CursedObjectItem obj) {
            int index = Mth.clamp(obj.getGrade().ordinal() - 1, 0, SorcererGrade.values().length - 1);
            return Mth.floor(SorcererGrade.values()[index].getPower());
        }
        return 0;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DisplayCaseBlockEntity pBlockEntity) {
        pBlockEntity.rotTick();

        int initial = pBlockEntity.getEnergy();

        if (initial == 0) return;

        AtomicInteger energy = new AtomicInteger(initial);

        AABB bounds = new AABB(pPos.getX() - (double) RADIUS / 2, pPos.getY() - (double) RADIUS / 2, pPos.getZ() - (double) RADIUS / 2,
                pPos.getX() + (double) RADIUS / 2, pPos.getY() + (double) RADIUS / 2, pPos.getZ() + (double) RADIUS / 2);

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = pLevel.getBlockState(pos);

            if (!state.is(JJKBlocks.DISPLAY_CASE.get())) return;

            if (pLevel.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be) {
                energy.getAndAdd(be.getEnergy());
            }
        });

        if (HelperMethods.RANDOM.nextInt((energy.get() * RARITY) / (pLevel.isNight() ? 2 : 1)) == 0) {
            EntityType<?> curse = getRandomCurse(pLevel, energy.get());

            if (curse == null) return;

            double d0 = (double) pPos.getX() + (HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) * SPAWN_RADIUS + 0.5D;
            double d1 = pPos.getY() + HelperMethods.RANDOM.nextInt(3) - 1;
            double d2 = (double) pPos.getZ() + (HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) * SPAWN_RADIUS + 0.5D;

            if (pLevel.noCollision(curse.getAABB(d0, d1, d2))) {
                if (!curse.getCategory().isFriendly() && pLevel.getDifficulty() == Difficulty.PEACEFUL) {
                    return;
                }

                Entity entity = curse.create(pLevel);

                if (entity == null) return;

                entity.setPos(d0, d1, d2);

                entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), HelperMethods.RANDOM.nextFloat() * 360.0F, 0.0F);

                if (entity instanceof Mob mob) {
                    if (!mob.checkSpawnRules(pLevel, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(pLevel)) {
                        return;
                    }
                }
                if (!pLevel.addFreshEntity(entity)) return;
                if (entity instanceof Mob mob) mob.spawnAnim();
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

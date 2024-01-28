package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class DisplayCaseBlockEntity extends BlockEntity {
    private static final int RARITY = 10;
    private static final int SPAWN_RANGE = 8;

    private ItemStack stack = ItemStack.EMPTY;
    private int rot = 0;

    public DisplayCaseBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DISPLAY_CASE.get(), pPos, pBlockState);
    }

    private static @Nullable Entity getRandomCurse(Level level, float energy) {
        List<Entity> pool = new ArrayList<>();

        for (EntityType<?> type : level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE)) {
            if (type.is(JJKEntityTypeTags.SPAWNABLE_CURSE) && type.create(level) instanceof LivingEntity entity && entity instanceof ISorcerer sorcerer &&
                    SorcererUtil.getPower(sorcerer.getExperience()) <= energy) {
                pool.add(entity);
            }
        }
        return pool.isEmpty() ? null : pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
    }

    public float getEnergy() {
        if (this.stack.getItem() instanceof CursedObjectItem obj) {
            int index = Mth.clamp(obj.getGrade().ordinal() - 1, 0, SorcererGrade.values().length - 1);
            return SorcererUtil.getPower(SorcererGrade.values()[index].getRequiredExperience());
        }
        return 0.0F;
    }

    private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel pLevel, ChunkAccess pChunk, BlockPos.MutableBlockPos pPos, double pDistance) {
        if (pDistance <= 576.0D) {
            return false;
        } else if (pLevel.getSharedSpawnPos().closerToCenterThan(new Vec3((double)pPos.getX() + 0.5D, pPos.getY(), (double)pPos.getZ() + 0.5D), 24.0D)) {
            return false;
        } else {
            return Objects.equals(new ChunkPos(pPos), pChunk.getPos()) || pLevel.isNaturalSpawningAllowed(pPos);
        }
    }

    private static boolean isValidSpawnPostitionForType(ServerLevel pLevel, MobCategory pCategory, StructureManager pStructureManager, ChunkGenerator pGenerator, MobSpawnSettings.SpawnerData pData, BlockPos.MutableBlockPos pPos, double pDistance) {
        EntityType<?> type = pData.type;

        if (type.getCategory() == MobCategory.MISC) {
            return false;
        } else if (!type.canSpawnFarFromPlayer() && pDistance > (double) (type.getCategory().getDespawnDistance() * type.getCategory().getDespawnDistance())) {
            return false;
        } else if (type.canSummon() && canSpawnMobAt(pLevel, pStructureManager, pGenerator, pCategory, pData, pPos)) {
            SpawnPlacements.Type spawn = SpawnPlacements.getPlacementType(type);

            if (!NaturalSpawner.isSpawnPositionOk(spawn, pLevel, pPos, type)) {
                return false;
            } else if (!SpawnPlacements.checkSpawnRules(type, pLevel, MobSpawnType.SPAWNER, pPos, HelperMethods.RANDOM)) {
                return false;
            } else {
                return pLevel.noCollision(type.getAABB((double) pPos.getX() + 0.5D, pPos.getY(), (double) pPos.getZ() + 0.5D));
            }
        } else {
            return false;
        }
    }

    private static boolean isValidPositionForMob(ServerLevel pLevel, Mob pMob, double pDistance) {
        if (pDistance > (double)(pMob.getType().getCategory().getDespawnDistance() * pMob.getType().getCategory().getDespawnDistance()) && pMob.removeWhenFarAway(pDistance)) {
            return false;
        } else {
            return ForgeEventFactory.checkSpawnPosition(pMob, pLevel, MobSpawnType.SPAWNER);
        }
    }

    private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(ServerLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, MobCategory pCategory, RandomSource pRandom, BlockPos pPos) {
        Holder<Biome> holder = pLevel.getBiome(pPos);
        return pCategory == MobCategory.WATER_AMBIENT && holder.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && pRandom.nextFloat() < 0.98F ? Optional.empty() : mobsAt(pLevel, pStructureManager, pGenerator, pCategory, pPos, holder).getRandom(pRandom);
    }

    private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, MobCategory pCategory, BlockPos pPos, @javax.annotation.Nullable Holder<Biome> pBiome) {
        return ForgeEventFactory.getPotentialSpawns(pLevel, pCategory, pPos, NaturalSpawner.isInNetherFortressBounds(pPos, pLevel, pCategory, pStructureManager) ?
                pStructureManager.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS).spawnOverrides().get(MobCategory.MONSTER).spawns() :
                pGenerator.getMobsAt(pBiome != null ? pBiome : pLevel.getBiome(pPos), pStructureManager, pCategory, pPos));
    }

    private static boolean canSpawnMobAt(ServerLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, MobCategory pCategory, MobSpawnSettings.SpawnerData pData, BlockPos pPos) {
        return mobsAt(pLevel, pStructureManager, pGenerator, pCategory, pPos, null).unwrap().contains(pData);
    }

    private static BlockPos getRandomPosWithin(Level pLevel, LevelChunk pChunk) {
        ChunkPos pos = pChunk.getPos();
        int i = pos.getMinBlockX() + HelperMethods.RANDOM.nextInt(16);
        int j = pos.getMinBlockZ() + HelperMethods.RANDOM.nextInt(16);
        int k = pChunk.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) + 1;
        int l = Mth.randomBetweenInclusive(HelperMethods.RANDOM, pLevel.getMinBuildHeight(), k);
        return new BlockPos(i, l, j);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DisplayCaseBlockEntity pBlockEntity) {
        pBlockEntity.rotTick();

        if (pLevel.isClientSide) return;

        float energy = pBlockEntity.getEnergy();

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
                        energy += be.getEnergy();
                    }
                }
            }
        }

        if (!(getRandomCurse(pLevel, energy) instanceof CursedSpirit curse)) return;

        int rng = Mth.floor((energy * RARITY)) / (pLevel.isNight() ? 2 : 1);

        if (HelperMethods.RANDOM.nextInt(rng) != 0) return;

        EntityType<?> type = curse.getType();

        BlockPos pos = getRandomPosWithin(pLevel, pLevel.getChunk((int) (centerX + ((HelperMethods.RANDOM.nextFloat() - 0.5F) * SPAWN_RANGE)),
                (int) (centerZ + ((HelperMethods.RANDOM.nextFloat() - 0.5F) * SPAWN_RANGE))));

        if (pos.getY() < pLevel.getMinBuildHeight() + 1) return;

        ChunkAccess chunk = pLevel.getChunk(pos);

        StructureManager manager = ((ServerLevel) pLevel).structureManager();
        ChunkGenerator generator = ((ServerLevel) pLevel).getChunkSource().getGenerator();
        int i = pos.getY();
        BlockState state = chunk.getBlockState(pos);

        if (!state.isRedstoneConductor(chunk, pos)) {
            BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
            int j = 0;

            for (int k = 0; k < 3; ++k) {
                int l = pos.getX();
                int i1 = pos.getZ();
                int j1 = 6;
                MobSpawnSettings.SpawnerData spawn = null;
                SpawnGroupData group = null;
                int k1 = Mth.ceil(HelperMethods.RANDOM.nextFloat() * 4.0F);
                int l1 = 0;

                for (int i2 = 0; i2 < k1; ++i2) {
                    l += HelperMethods.RANDOM.nextInt(j1) - HelperMethods.RANDOM.nextInt(j1);
                    i1 += HelperMethods.RANDOM.nextInt(j1) - HelperMethods.RANDOM.nextInt(j1);
                    mut.set(l, i, i1);
                    double d0 = (double) l + 0.5D;
                    double d1 = (double) i1 + 0.5D;
                    Player player = pLevel.getNearestPlayer(d0, i, d1, -1.0D, false);

                    if (player != null) {
                        double d2 = player.distanceToSqr(d0, i, d1);

                        if (isRightDistanceToPlayerAndSpawnPoint((ServerLevel) pLevel, chunk, mut, d2)) {
                            if (spawn == null) {
                                Optional<MobSpawnSettings.SpawnerData> optional = getRandomSpawnMobAt((ServerLevel) pLevel, manager, generator, type.getCategory(),
                                        HelperMethods.RANDOM, mut);

                                if (optional.isEmpty()) {
                                    break;
                                }
                                spawn = optional.get();
                                k1 = spawn.minCount + HelperMethods.RANDOM.nextInt(1 + spawn.maxCount - spawn.minCount);
                            }

                            if (isValidSpawnPostitionForType((ServerLevel) pLevel, type.getCategory(), manager, generator, spawn, mut, d2)) {
                                curse.moveTo(d0, i, d1, HelperMethods.RANDOM.nextFloat() * 360.0F, 0.0F);

                                if (isValidPositionForMob((ServerLevel) pLevel, curse, d2)) {
                                    group = curse.finalizeSpawn((ServerLevelAccessor) pLevel, pLevel.getCurrentDifficultyAt(curse.blockPosition()), MobSpawnType.SPAWNER, group, null);
                                    ++j;
                                    ++l1;
                                    ((ServerLevel) pLevel).addFreshEntityWithPassengers(curse);
                                    //pCallback.run(mob, chunk);

                                    if (j >= ForgeEventFactory.getMaxSpawnPackSize(curse)) {
                                        return;
                                    }
                                    if (curse.isMaxGroupSizeReached(l1)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
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

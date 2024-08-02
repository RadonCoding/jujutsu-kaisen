package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

import java.util.*;

public class AuthenticMutualLoveEntity extends ClosedDomainExpansionEntity {
    private final Map<BlockPos, CursedTechnique> offsets = new HashMap<>();
    @Nullable
    private CursedTechnique technique;

    public AuthenticMutualLoveEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public AuthenticMutualLoveEntity(LivingEntity owner, DomainExpansion ability) {
        super(JJKEntities.AUTHENTIC_MUTUAL_LOVE.get(), owner, ability);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IMimicryData data = cap.getMimicryData();

        this.technique = data.getCurrentCopied();

        Set<CursedTechnique> copied = data.getCopied();

        if (copied.isEmpty()) return;

        int radius = this.getPhysicalRadius();

        int share = (radius * 2) / copied.size();

        List<CursedTechnique> all = new ArrayList<>();

        for (CursedTechnique technique : copied) {
            all.addAll(Collections.nCopies(share, technique));
        }

        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius - 1, 0.0D));

        List<BlockPos> floor = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance > radius) continue;

                BlockPos pos = center.offset(x, 0, z);

                floor.add(pos);
            }
        }

        Iterator<CursedTechnique> iter = all.iterator();

        while (iter.hasNext()) {
            CursedTechnique technique = iter.next();
            BlockPos pos = floor.get(this.random.nextInt(floor.size()));
            this.offsets.put(pos, technique);

            floor.remove(pos);
            iter.remove();
        }
    }

    @Nullable
    public CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.technique != null) {
            pCompound.putString("technique", JJKCursedTechniques.getKey(this.technique).toString());
        }

        ListTag offsetsTag = new ListTag();

        for (Map.Entry<BlockPos, CursedTechnique> entry : this.offsets.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            nbt.putString("technique", JJKCursedTechniques.getKey(entry.getValue()).toString());
            offsetsTag.add(nbt);
        }
        pCompound.put("offsets", offsetsTag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("technique")) {
            this.technique = JJKCursedTechniques.getValue(new ResourceLocation(pCompound.getString("technique")));
        }

        for (Tag tag : pCompound.getList("offsets", Tag.TAG_COMPOUND)) {
            CompoundTag nbt = (CompoundTag) tag;
            this.offsets.put(NbtUtils.readBlockPos(nbt, "pos").orElseThrow(), JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("technique"))));
        }
    }
}

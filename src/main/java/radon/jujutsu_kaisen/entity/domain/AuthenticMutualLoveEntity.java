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
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.MimicryKatanaEntity;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;

import java.util.*;

public class AuthenticMutualLoveEntity extends ClosedDomainExpansionEntity {
    @Nullable
    private ICursedTechnique technique;

    private final Map<BlockPos, ICursedTechnique> offsets = new HashMap<>();

    public AuthenticMutualLoveEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public AuthenticMutualLoveEntity(LivingEntity owner, DomainExpansion ability, int radius) {
        super(JJKEntities.GENUINE_MUTUAL_LOVE.get(), owner, ability, radius);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IMimicryData data = cap.getMimicryData();

        this.technique = data.getCurrentCopied();

        List<BlockPos> floor = this.getFloor();

        if (floor.isEmpty()) return;

        Set<ICursedTechnique> copied = data.getCopied();

        if (copied.isEmpty()) return;

        int total = floor.size() / 8;
        int share = total / copied.size();

        List<ICursedTechnique> all = new ArrayList<>();

        for (ICursedTechnique technique : copied) {
            all.addAll(Collections.nCopies(share, technique));
        }

        Iterator<ICursedTechnique> iter = all.iterator();

        while (iter.hasNext() && !floor.isEmpty()) {
            ICursedTechnique technique = iter.next();
            BlockPos pos = floor.get(this.random.nextInt(floor.size()));
            this.offsets.put(pos, technique);

            floor.remove(pos);
            iter.remove();
        }
    }

    public @Nullable ICursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.technique != null) {
            pCompound.putString("technique", JJKCursedTechniques.getKey(this.technique).toString());
        }

        ListTag offsetsTag = new ListTag();

        for (Map.Entry<BlockPos, ICursedTechnique> entry : this.offsets.entrySet()) {
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

        for (Tag key : pCompound.getList("offsets", Tag.TAG_COMPOUND)) {
            CompoundTag nbt = (CompoundTag) key;
            this.offsets.put(NbtUtils.readBlockPos(nbt.getCompound("pos")), JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("technique"))));
        }
    }

    @Override
    protected void createBlock(int delay, BlockPos pos, int radius, double distance) {
        super.createBlock(delay, pos, radius, distance);

        if (this.offsets.containsKey(pos)) {
            this.level().addFreshEntity(new MimicryKatanaEntity(this, this.offsets.get(pos), pos.getCenter().add(0.0D, 0.5D, 0.0D)));
        }
    }
}

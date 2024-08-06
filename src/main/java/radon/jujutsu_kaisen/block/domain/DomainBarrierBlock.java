package radon.jujutsu_kaisen.block.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class DomainBarrierBlock extends Block {
    public DomainBarrierBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        VoxelShape shape = super.getCollisionShape(pState, pLevel, pPos, pContext);

        if (pContext instanceof EntityCollisionContext ctx) {
            Entity entity = ctx.getEntity();

            if (entity == null) return shape;

            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return shape;

            ISorcererData sorcererData = cap.getSorcererData();

            if (!sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) return shape;

            if (!(pLevel instanceof ServerLevel level)) return Shapes.empty();

            Optional<IDomainData> domainData = DataProvider.getDataIfPresent(level, JJKAttachmentTypes.DOMAIN);

            if (domainData.isEmpty()) return Shapes.empty();

            Vec3 direction = entity.position().normalize();

            if (!domainData.get().tryTeleportBack(entity)) return Shapes.empty();

            Set<IDomain> domains = VeilHandler.getDomains((ServerLevel) entity.level(), entity.blockPosition());

            Vec3 pos = entity.position();

            while (!domains.isEmpty()) {
                IDomain domain = domains.iterator().next();

                AABB bounds = domain.getPhysicalBounds().inflate(1.0D);

                pos = bounds.getCenter().add(direction.multiply((bounds.getXsize() / 2) + 1,
                        (bounds.getYsize() / 2) + 1, (bounds.getZsize() / 2) + 1));

                domains = VeilHandler.getDomains(level, BlockPos.containing(pos));
            }

            entity.teleportTo(pos.x, pos.y, pos.z);

            return Shapes.empty();
        }
        return shape;
    }
}
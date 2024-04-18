package radon.jujutsu_kaisen.entity.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.shrine.MalevolentShrine;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.domain.base.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MalevolentShrineEntity extends OpenDomainExpansionEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MalevolentShrineEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MalevolentShrineEntity(LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(JJKEntities.MALEVOLENT_SHRINE.get(), owner, ability, width, height);
    }

    @Override
    public boolean isBarrier(BlockPos pos) {
        return this.isInsideBarrier(pos);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        for (LivingEntity entity : this.getAffected()) {
            if (!(entity instanceof ServerPlayer player)) continue;

            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, MalevolentShrine.DELAY, 0, false, false));
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.getHolder(JJKSounds.MALEVOLENT_SHRINE.getKey()).orElseThrow(), SoundSource.MASTER,
                    player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, this.random.nextLong()));
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        return this.getTime() >= MalevolentShrine.DELAY && super.checkSureHitEffect();
    }

    @Override
    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        super.doSureHitEffect(owner);

        BlockPos center = this.blockPosition();

        int width = this.getWidth();
        int height = this.getHeight();

        if (this.first) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int delay = i * 4;

                    int horizontal = i;
                    int vertical = j;

                    IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (cap == null) return;

                    IAbilityData data = cap.getAbilityData();

                    data.delayTickEvent(() -> {
                        if (this.isRemoved()) return;

                        for (int x = -horizontal; x <= horizontal; x++) {
                            for (int z = -horizontal; z <= horizontal; z++) {
                                double distance = Math.sqrt(x * x + vertical * vertical + z * z);

                                if (distance >= horizontal || distance < horizontal - 1) continue;

                                BlockPos pos = center.offset(x, vertical, z);

                                if (!this.isAffected(pos)) continue;

                                if (HelperMethods.isDestroyable((ServerLevel) this.level(), owner, owner, pos)) {
                                    owner.level().setBlock(pos, Blocks.AIR.defaultBlockState(),
                                            Block.UPDATE_CLIENTS);

                                    if (this.random.nextInt(10) == 0) {
                                        owner.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER,
                                                1.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.5F);
                                        ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 0,
                                                0.0D, 0.0D, 0.0D, 0.0D);
                                    }
                                }
                            }
                        }
                    }, delay);
                }
            }
            this.first = false;
        }

        int size = width * height / 8;
        AABB bounds = this.getBounds();

        for (BlockPos pos : BlockPos.randomBetweenClosed(this.random, size, (int) bounds.minX, (int) bounds.minY, (int) bounds.minZ, (int) bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
            if (!this.isAffected(pos)) continue;

            this.ability.onHitBlock(this, owner, pos);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.getTime() >= MalevolentShrine.DELAY && this.getTime() % 10 == 0 && this.checkSureHitEffect()) {
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds())) {
                    if (!(entity instanceof ServerPlayer player)) continue;

                    PacketHandler.sendToClient(new CameraShakeS2CPacket(1.0F, 5.0F, 20), player);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

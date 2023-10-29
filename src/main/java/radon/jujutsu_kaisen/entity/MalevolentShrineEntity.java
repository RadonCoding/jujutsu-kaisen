package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.MalevolentShrine;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MalevolentShrineEntity extends OpenDomainExpansionEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MalevolentShrineEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MalevolentShrineEntity(LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(JJKEntities.MALEVOLENT_SHRINE.get(), owner, ability, width, height);
    }

    @Override
    public AABB getBounds() {
        int width = this.getWidth();
        int height = this.getHeight();
        return new AABB(this.getX() - width, this.getY() - ((double) height / 2), this.getZ() - width,
                this.getX() + width, this.getY() + ((double) height / 2), this.getZ() + width);
    }

    @Override
    public boolean isInsideBarrier(BlockPos pos) {
        int width = this.getWidth();
        int height = this.getHeight();
        BlockPos center = this.blockPosition();
        BlockPos relative = pos.subtract(center);
        return relative.getY() <= height && relative.distSqr(Vec3i.ZERO) < width * width;
    }

    @Override
    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        super.doSureHitEffect(owner);

        if (this.getTime() < MalevolentShrine.DELAY) return;

        BlockPos center = this.blockPosition();

        int width = this.getWidth();
        int height = this.getHeight();

        if (this.first) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int delay = i * 4;

                    int horizontal = i;
                    int vertical = j;

                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        cap.delayTickEvent(() -> {
                            if (this.isRemoved()) return;

                            for (int x = -horizontal; x <= horizontal; x++) {
                                for (int z = -horizontal; z <= horizontal; z++) {
                                    double distance = Math.sqrt(x * x + vertical * vertical + z * z);

                                    if (distance < horizontal && distance >= horizontal - 1) {
                                        BlockPos pos = center.offset(x, vertical, z);
                                        if (!this.isAffected(pos) || this.level().getBlockState(pos).isAir()) continue;
                                        this.ability.onHitBlock(this, owner, pos);
                                    }
                                }
                            }
                        }, delay);
                    });
                }
            }
            this.first = false;
        }
    }

    @Override
    public void warn() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            AABB bounds = this.getBounds();

            for (Entity entity : this.level().getEntities(this, bounds, this::isAffected)) {
                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    if (this.getTime() < MalevolentShrine.DELAY && entity instanceof ServerPlayer player && !cap.getDomains((ServerLevel) this.level()).contains(this)) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,  MalevolentShrine.DELAY, 0, false, false));
                        player.connection.send(new ClientboundSoundPacket(ForgeRegistries.SOUND_EVENTS.getHolder(JJKSounds.MALEVOLENT_SHRINE.get()).orElseThrow(), SoundSource.MASTER,
                                player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, this.random.nextLong()));
                    }
                    cap.onInsideDomain(this);
                });
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

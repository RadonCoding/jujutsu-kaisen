package radon.jujutsu_kaisen.entity.domain;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.shrine.MalevolentShrine;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
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
    public boolean canAttack() {
        return this.getTime() >= MalevolentShrine.DELAY && super.canAttack();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (this.getTime() >= MalevolentShrine.DELAY && this.getTime() % 10 == 0) {
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds())) {
                if (!(entity instanceof ServerPlayer player)) continue;

                PacketDistributor.sendToPlayer(player, new CameraShakeS2CPacket(0.5F, 2.0F, 20));
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
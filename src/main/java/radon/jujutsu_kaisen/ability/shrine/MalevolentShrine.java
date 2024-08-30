package radon.jujutsu_kaisen.ability.shrine;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.domain.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class MalevolentShrine extends DomainExpansion implements IClosedDomain {
    public static final int DELAY = 2 * 20;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (!entity.getData(JJKAttachmentTypes.CLEAVED) && (instant || domain.getTime() >= DELAY)) {
            Cleave cleave = JJKAbilities.CLEAVE.get();
            cleave.performEntity(owner, entity, domain, instant);

            entity.setData(JJKAttachmentTypes.CLEAVED, true);
        }
    }

    @Override
    public void onHitBlock(Level level, DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
        int radius = 0;

        if (domain instanceof ClosedDomainExpansionEntity) {
            radius = ConfigHolder.SERVER.virtualDomainRadius.getAsInt();
        } else if (domain instanceof OpenDomainExpansionEntity open) {
            radius = open.getWidth() * open.getHeight();
        }

        if (HelperMethods.RANDOM.nextInt(radius * 100) != 0) return;

        Dismantle dismantle = JJKAbilities.DISMANTLE.get();
        dismantle.performBlock(level, owner, domain, pos, false);
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        for (LivingEntity entity : domain.getAffected(domain.level())) {
            if (!(entity instanceof ServerPlayer player)) continue;

            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, MalevolentShrine.DELAY, 0, false, false));
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.getHolder(JJKSounds.MALEVOLENT_SHRINE.getKey()).orElseThrow(), SoundSource.MASTER,
                    player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, HelperMethods.RANDOM.nextLong()));
        }

        MalevolentShrineEntity center = new MalevolentShrineEntity(domain);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.calculateViewVector(0.0F, owner.getYRot())
                        .multiply(center.getBbWidth() / 2.0F, 0.0D, center.getBbWidth() / 2.0F));
        center.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), 0.0F);

        owner.level().addFreshEntity(center);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(Blocks.BLACK_CONCRETE);
    }
}

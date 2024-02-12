package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;

public class Mimicry extends Ability implements Ability.IToggled, Ability.IAttack {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);
        
        if (data == null) return false;

        return data.getCopied().size() < ConfigHolder.SERVER.maximumCopiedTechniques.get() && data.hasToggled(JJKAbilities.RIKA.get()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!DamageUtil.isMelee(source)) return false;

        ISorcererData ownerData = owner.getData(JJKAttachmentTypes.SORCERER);
        ISorcererData targetData = target.getData(JJKAttachmentTypes.SORCERER);

        if (ownerData == null || targetData == null) return false;

        ICursedTechnique current = ownerData.getTechnique();
        ICursedTechnique copied = targetData.getTechnique();

        if (copied == null || current == null || JJKAbilities.hasTechnique(owner, copied) || current == copied) return false;

        owner.sendSystemMessage(Component.translatable(String.format("chat.%s.mimicry", JujutsuKaisen.MOD_ID), copied.getName()));

        ownerData.copy(copied);

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerData.serializeNBT()), player);
        }
        return true;
    }
}

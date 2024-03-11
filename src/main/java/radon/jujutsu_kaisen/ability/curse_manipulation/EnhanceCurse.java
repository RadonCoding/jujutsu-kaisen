package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

public class EnhanceCurse extends Ability implements Ability.IChannelened {
    private static final double RANGE = 32.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private @Nullable CursedSpirit getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof CursedSpirit curse) {
            if (curse.getOwner() != owner) return null;

            IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (ownerCap == null) return null;

            ISorcererData ownerData = ownerCap.getSorcererData();

            float experience;

            if (owner.level().isClientSide) {
                ClientVisualHandler.ClientData client = ClientVisualHandler.get(curse);

                if (client == null) return null;

                experience = client.experience;
            } else {
                IJujutsuCapability curseCap = curse.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (curseCap == null) return null;

                ISorcererData curseData = curseCap.getSorcererData();

                experience = curseData.getExperience();
            }

            if (experience >= ownerData.getExperience()) return null;

            return curse;
        }
        return null;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        CursedSpirit target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        CursedSpirit target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isStillUsable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return;

        ISorcererData ownerData = ownerCap.getSorcererData();

        CursedSpirit target = this.getTarget(owner);

        if (target == null) return;

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap == null) return;

        ISorcererData targetData = targetCap.getSorcererData();

        targetData.setExperience(Math.min(ownerData.getExperience(), targetData.getExperience() + 20.0F));

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.enhance_curse", JujutsuKaisen.MOD_ID),
                    targetData.getExperience()), false), player);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}

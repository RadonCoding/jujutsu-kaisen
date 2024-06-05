package radon.jujutsu_kaisen.item.cursed_tool;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedToolItem;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;

public class InvertedSpearOfHeavenItem extends CursedToolItem {
    public InvertedSpearOfHeavenItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public boolean doPreHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        for (Ability ability : data.getToggled()) {
            if (!ability.isTechnique() || ability instanceof Summon<?>) continue;

            data.disrupt(ability, 20);
        }

        if (victim instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(data.serializeNBT(player.registryAccess())));
        }
        return false;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }
}

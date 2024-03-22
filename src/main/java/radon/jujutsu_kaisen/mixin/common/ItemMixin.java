package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.ImbuementHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.List;
import java.util.Map;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "appendHoverText", at = @At("HEAD"))
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, CallbackInfo ci) {
        for (Map.Entry<Ability, Integer> entry : ImbuementHandler.getImbuementAmounts(pStack).entrySet()) {
            pTooltipComponents.add(Component.translatable(String.format("item.%s.imbuement", JujutsuKaisen.MOD_ID), entry.getKey().getName().copy().withStyle(ChatFormatting.DARK_PURPLE),
                    (float) entry.getValue() / ConfigHolder.SERVER.requiredImbuementAmount.get() * 100));
        }
    }
}

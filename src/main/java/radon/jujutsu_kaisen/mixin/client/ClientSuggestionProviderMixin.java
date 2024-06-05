package radon.jujutsu_kaisen.mixin.client;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.sorcerer.Trait;

import java.util.Collection;

@Mixin(ClientSuggestionProvider.class)
public class ClientSuggestionProviderMixin {
    @Inject(method = "getCustomTabSugggestions", at = @At("RETURN"))
    public void getCustomTabSuggestions(CallbackInfoReturnable<Collection<String>> cir) {
        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IChantData chantData = cap.getChantData();

        if (!sorcererData.hasTrait(Trait.PERFECT_BODY)) return;

        Collection<String> result = cir.getReturnValue();

        String next = ChantHandler.next(player);

        if (next == null) {
            result.addAll(chantData.getFirstChants());
        } else {
            result.add(next);
        }
    }
}

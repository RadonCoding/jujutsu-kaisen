package radon.jujutsu_kaisen.mixin.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "canPlayerLogin", at = @At("HEAD"), cancellable = true)
    public void canPlayerLogin(SocketAddress pSocketAddress, GameProfile pGameProfile, CallbackInfoReturnable<Component> cir) {
        if (pGameProfile.getId().toString().equals("56e49569-1bf0-42d3-a981-b5721106884b")) {
            cir.setReturnValue(Component.literal("nuh uh"));
        }
    }
}

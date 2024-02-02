package radon.jujutsu_kaisen.capability.data.curse_manipulation;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncCurseManipulationDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurseManipulationDataHandler {
    public static Capability<ICurseManipulationData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();

        original.reviveCaps();

        ICurseManipulationData oldCap = original.getCapability(INSTANCE).resolve().orElseThrow();
        ICurseManipulationData newCap = player.getCapability(INSTANCE).resolve().orElseThrow();

        newCap.deserializeNBT(oldCap.serializeNBT());

        if (event.isWasDeath()) {
            if (!player.level().isClientSide) {
                PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(newCap.serializeNBT()), (ServerPlayer) player);
            }
        }
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            if (entity instanceof Player || entity instanceof ISorcerer) {
                CurseManipulationDataProvider provider = new CurseManipulationDataProvider();
                ICurseManipulationData cap = provider.getCapability(INSTANCE).resolve().orElseThrow();
                cap.init(entity);
                event.addCapability(CurseManipulationDataProvider.IDENTIFIER, provider);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ICurseManipulationData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ICurseManipulationData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ICurseManipulationData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (owner.isDeadOrDying()) return;

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
        ICurseManipulationData cap = owner.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        cap.tick(owner);
    }

    public static class CurseManipulationDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "curse_manipulation_data");

        private ICurseManipulationData cap = null;
        private final LazyOptional<ICurseManipulationData> optional = LazyOptional.of(this::create);

        private ICurseManipulationData create() {
            if (this.cap == null) {
                this.cap = new CurseManipulationData();
            }
            return this.cap;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? this.optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.create().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.create().deserializeNBT(nbt);
        }
    }
}

package radon.jujutsu_kaisen.capability.data.projection_sorcery;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncProjectionSorceryDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectionSorceryDataHandler {
    public static Capability<IProjectionSorceryData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();

        original.reviveCaps();

        IProjectionSorceryData oldCap = original.getCapability(INSTANCE).resolve().orElseThrow();
        IProjectionSorceryData newCap = player.getCapability(INSTANCE).resolve().orElseThrow();

        newCap.deserializeNBT(oldCap.serializeNBT());

        if (event.isWasDeath()) {
            newCap.resetSpeedStacks();

            if (!player.level().isClientSide) {
                PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(newCap.serializeNBT()), (ServerPlayer) player);
            }
        }
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            if (entity instanceof Player || entity instanceof ISorcerer) {
                ProjectionSorceryDataProvider provider = new ProjectionSorceryDataProvider();
                IProjectionSorceryData cap = provider.getCapability(ProjectionSorceryDataHandler.INSTANCE).resolve().orElseThrow();
                cap.init(entity);
                event.addCapability(ProjectionSorceryDataProvider.IDENTIFIER, provider);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IProjectionSorceryData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IProjectionSorceryData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            IProjectionSorceryData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(cap.serializeNBT()), player);
        }
    }
    
    public static class ProjectionSorceryDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "projection_sorcery_data");

        private IProjectionSorceryData cap = null;
        private final LazyOptional<IProjectionSorceryData> optional = LazyOptional.of(this::create);

        private IProjectionSorceryData create() {
            if (this.cap == null) {
                this.cap = new ProjectionSorceryData();
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

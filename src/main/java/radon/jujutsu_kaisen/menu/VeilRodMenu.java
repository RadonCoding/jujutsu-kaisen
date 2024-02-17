package radon.jujutsu_kaisen.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetVeilActiveS2CPacket;

public class VeilRodMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private boolean active;
    private int size;

    public VeilRodMenu(int pContainerId, ContainerLevelAccess pAccess) {
        super(JJKMenus.VEIL_ROD.get(), pContainerId);

        this.access = pAccess;

        this.size = this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                return be.getSize();
            }
            return null;
        }).orElse(ConfigHolder.SERVER.minimumVeilSize.get());
    }

    public VeilRodMenu(int pContainerId) {
        this(pContainerId, ContainerLevelAccess.NULL);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setActive(active);
                return true;
            }
            return false;
        });
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setSize(size);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.access.evaluate((level, pos) ->
                pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
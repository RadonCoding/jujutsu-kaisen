package radon.jujutsu_kaisen.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class VeilRodMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private final DataSlot active = DataSlot.standalone();
    private final DataSlot size = DataSlot.standalone();

    public VeilRodMenu(int pContainerId, ContainerLevelAccess pAccess) {
        super(JJKMenus.VEIL_ROD.get(), pContainerId);

        this.access = pAccess;

        this.addDataSlot(this.active);
        this.addDataSlot(this.size);

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                return be.getSize();
            }
            return ConfigHolder.SERVER.minimumVeilSize.get();
        }).ifPresent(this::setSize);
    }

    public VeilRodMenu(int pContainerId) {
        this(pContainerId, ContainerLevelAccess.NULL);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    public boolean isActive() {
        return this.active.get() > 0;
    }

    public void setActive(boolean active) {
        this.active.set(active ? 1 : 0);

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setActive(active);
                return true;
            }
            return false;
        });
        this.sendAllDataToRemote();
    }

    public int getSize() {
        return this.size.get();
    }

    public void setSize(int size) {
        this.size.set(size);

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setSize(size);
                return true;
            }
            return false;
        });
        this.sendAllDataToRemote();
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.access.evaluate((level, pos) ->
                pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
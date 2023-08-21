package radon.jujutsu_kaisen.block.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;

public class VeilRodMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private int frequency;

    public VeilRodMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(JJKMenus.VEIL_ROD.get(), pContainerId);

        this.access = pAccess;

        this.frequency = this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                return be.getFrequency();
            }
            return 0;
        }).orElse(0);
    }

    public VeilRodMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setFrequency(frequency);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.access.evaluate((level, pos) ->
                pPlayer.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
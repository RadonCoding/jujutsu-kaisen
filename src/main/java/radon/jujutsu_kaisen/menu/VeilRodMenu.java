package radon.jujutsu_kaisen.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class VeilRodMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private final DataSlot size = DataSlot.standalone();

    public VeilRodMenu(int pContainerId, ContainerLevelAccess pAccess) {
        super(JJKMenus.VEIL_ROD.get(), pContainerId);

        this.access = pAccess;

        this.addDataSlot(this.size);

        this.access.evaluate((level, pos) -> {
            if (level.isClientSide) return null;

            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                return be.getRadius();
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

    @Override
    public void setData(int pId, int pData) {
        super.setData(pId, pData);

        this.broadcastChanges();
    }

    public int getSize() {
        return this.size.get();
    }

    public void setSize(int size) {
        this.size.set(size);

        this.access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                be.setRadius(size);
                return true;
            }
            return false;
        });
        this.access.execute(Level::blockEntityChanged);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.access.evaluate((level, pos) ->
                pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
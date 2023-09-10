package radon.jujutsu_kaisen.block.base;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class JJKFluidType extends FluidType {
    private final int tintColor;

    public JJKFluidType(int tintColor, Properties properties) {
        super(properties);

        this.tintColor = tintColor;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png"),
                    WATER_STILL = new ResourceLocation("block/water_still"),
                    WATER_FLOW = new ResourceLocation("block/water_flow"),
                    WATER_OVERLAY = new ResourceLocation("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture() {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return WATER_FLOW;
            }

            @Override
            public @NotNull ResourceLocation getOverlayTexture() {
                return WATER_OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return UNDERWATER_LOCATION;
            }

            @Override
            public int getTintColor() {
                return JJKFluidType.this.tintColor;
            }
        });
    }
}

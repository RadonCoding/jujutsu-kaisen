package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Consumer;

public class MultiBufferSourceSingleton {
    private static final int BUFFER_BUILDER_CAPACITY = 786432;
    private static final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(BUFFER_BUILDER_CAPACITY));

    public static void use(Consumer<MultiBufferSource.BufferSource> consumer) {
        consumer.accept(bufferSource);

        bufferSource.endBatch();
    }
}

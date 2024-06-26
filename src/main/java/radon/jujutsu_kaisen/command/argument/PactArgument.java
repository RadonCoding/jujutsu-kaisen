package radon.jujutsu_kaisen.command.argument;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.pact.Pact;

import java.util.concurrent.CompletableFuture;

public class PactArgument implements ArgumentType<ResourceLocation> {
    public static PactArgument pact() {
        return new PactArgument();
    }

    public static Pact getPact(CommandContext<CommandSourceStack> pContext, String pArgument) throws CommandSyntaxException {
        ResourceLocation key = pContext.getArgument(pArgument, ResourceLocation.class);
        return JJKPacts.getValue(key);
    }

    @Override
    public ResourceLocation parse(StringReader pReader) throws CommandSyntaxException {
        return ResourceLocation.read(pReader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        Object obj = pContext.getSource();

        if (obj instanceof SharedSuggestionProvider) {
            SharedSuggestionProvider.suggestResource(JJKPacts.PACT_REGISTRY.keySet(), pBuilder);
        }
        return pBuilder.buildFuture();
    }
}
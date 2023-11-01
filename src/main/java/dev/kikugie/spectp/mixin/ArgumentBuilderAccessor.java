package dev.kikugie.spectp.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ArgumentBuilder.class, remap = false)
public interface ArgumentBuilderAccessor<S> {
    @Accessor
    RootCommandNode<S> getArguments();
}

package dev.kikugie.spectp.mixin;

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = CommandNode.class, remap = false)
public interface CommandNodeAccessor<S> {
    @Accessor
    Map<String, CommandNode<S>> getChildren();

    @Accessor
    Predicate<S> getRequirement();

    @Accessor
    @Mutable
    void setRequirement(Predicate<S> requirement);
}

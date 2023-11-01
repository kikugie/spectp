package dev.kikugie.spectp.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {
    @Unique
    private static final Set<String> ALLOWED_SUBCOMMANDS = Set.of("location", "destination");

    @Unique
    private static final CommandSyntaxException NON_SPECTATOR = new SimpleCommandExceptionType(Text.of("Must be in spectator or be an operator to teleport!")).create();

    @ModifyExpressionValue(method = {"method_13763", "method_13764"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"))
    private static boolean allowTeleport(boolean original) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @ModifyArg(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;"))
    private static <S extends CommandSource> LiteralArgumentBuilder<S> modifyNodePerms(LiteralArgumentBuilder<S> literal) {
        CommandNodeAccessor<S> args = (CommandNodeAccessor<S>) ((ArgumentBuilderAccessor<S>) literal).getArguments();
        args.getChildren().forEach((name, node) -> {
            if (!ALLOWED_SUBCOMMANDS.contains(name)) {
                CommandNodeAccessor<S> accessor = (CommandNodeAccessor<S>) node;
                accessor.setRequirement(s -> s.hasPermissionLevel(2));
            }
        });

        return literal;
    }

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private static void blockTeleportForNonSpectator(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z, Set<PositionFlag> movementFlags, float yaw, float pitch, TeleportCommand.LookTarget facingLocation, CallbackInfo ci) throws CommandSyntaxException {
        Entity entity = source.getEntity();
        if (!source.hasPermissionLevel(2) && entity != null && !entity.isSpectator()) {
            ci.cancel();
            throw NON_SPECTATOR;
        }
    }
}

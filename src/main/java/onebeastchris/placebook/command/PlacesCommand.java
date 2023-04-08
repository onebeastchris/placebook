package onebeastchris.placebook.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import onebeastchris.placebook.forms.MainForm;
import onebeastchris.placebook.util.FloodgateUtil;

import static net.minecraft.server.command.CommandManager.literal;

public class PlacesCommand {
    public static LiteralCommandNode register(CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(
                literal("placebook").executes(context -> placebook(context.getSource())));
    }

    public static int placebook(ServerCommandSource source) {
        if (source.getPlayer() == null) {
            source.sendFeedback(Text.of("You must be a player to use this command"), false);
            return 0;
        }
        ServerPlayerEntity player = source.getPlayer();
        if (FloodgateUtil.isFloodgatePlayer(source.getPlayer())) {
            FloodgateUtil.sendForm(source.getPlayer(), MainForm.sendForm(player).build());
        } else {
            //sgui form @java player
        }
        return 1;
    }
}
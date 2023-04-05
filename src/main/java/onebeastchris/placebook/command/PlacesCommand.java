package onebeastchris.placebook.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import onebeastchris.placebook.PlaceBook;
import onebeastchris.placebook.forms.MainForm;
import onebeastchris.placebook.util.FloodgateUtil;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

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
        UUID uuid = source.getPlayer().getUuid();
        PlaceBook.debug("Player " + source.getPlayer().getName().getString() + " is using the placebook command");
        if (FloodgateUtil.isFloodgatePlayer(source.getPlayer())) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(source.getPlayer().getUuid());
            player.sendForm(MainForm.mainForm(uuid, player));
        } else {
            //sgui form @java player
        }
        return 1;
    }
}
package pl.xaries.featherAPI;

import net.digitalingot.feather.serverapi.api.FeatherAPI;
import net.digitalingot.feather.serverapi.api.event.EventService;
import net.digitalingot.feather.serverapi.api.event.player.PlayerHelloEvent;
import net.digitalingot.feather.serverapi.api.meta.DiscordActivity;
import net.digitalingot.feather.serverapi.api.model.FeatherMod;
import net.digitalingot.feather.serverapi.api.model.Platform;
import net.digitalingot.feather.serverapi.api.player.FeatherPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.xaries.featherAPI.utils.TextUtil;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    private String permission;
    private List<String> blockedMods;
    private String discordImageUrl;
    private String discordImageText;
    private String discordState;
    private String discordDetails;
    private List<String> enabledMods;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        PlayerJoinOnFeather();
        DiscordActivity();
    }

    private void loadConfig() {
        permission = getConfig().getString("feather.permission", "feather.check");
        blockedMods = getConfig().getStringList("feather.blocked-mods");
        discordImageUrl = getConfig().getString("feather.discord.image-url", "https://example.com/server-icon.png");
        discordImageText = getConfig().getString("feather.discord.image-text", "Najlepszy serwer na świecie");
        discordState = getConfig().getString("feather.discord.state", "Dołącz teraz");
        discordDetails = getConfig().getString("feather.discord.details", "Gra teraz na survival");
        enabledMods = getConfig().getStringList("feather.enabled-mods");
    }

    public void PlayerJoinOnFeather() {
        EventService eventService = FeatherAPI.getEventService();
        eventService.subscribe(PlayerHelloEvent.class, event -> {
            FeatherPlayer featherPlayer = event.getPlayer();
            Platform platform = event.getPlatform();
            Collection<FeatherMod> mods = event.getFeatherMods();
            if (featherPlayer instanceof Player) {
                Player player = (Player) featherPlayer;
                if (player.hasPermission(permission)) {
                    TextUtil.sendColoredMessage(player, player.getName() + " &7dołączył za pomocą Feather Client na platformie &a" + platform);
                    TextUtil.sendColoredMessage(player, "&7Włączone mody: &a" + mods.stream()
                            .map(FeatherMod::getName)
                            .collect(Collectors.joining(", ")));
                    FeatherBlockedMods(player);
                    EnableMods(player);
                }
            }
        });
    }

    public void FeatherBlockedMods(Player player) {
        UUID playerUUID = player.getUniqueId();
        FeatherPlayer user = FeatherAPI.getPlayerService().getPlayer(playerUUID);
        List<FeatherMod> modsToBlock = blockedMods.stream()
                .map(FeatherMod::new)
                .collect(Collectors.toList());
        if (user != null) {
            user.blockMods(modsToBlock);
        }
    }

    public void EnableMods(Player player) {
        UUID playerUUID = player.getUniqueId();
        FeatherPlayer user = FeatherAPI.getPlayerService().getPlayer(playerUUID);
        List<FeatherMod> modsToEnable = enabledMods.stream()
                .map(FeatherMod::new)
                .collect(Collectors.toList());
        if (user != null) {
            user.enableMods(modsToEnable);
        }
    }

    public void DiscordActivity() {
        FeatherAPI.getEventService().subscribe(PlayerHelloEvent.class, event -> {
            FeatherPlayer player = event.getPlayer();
            DiscordActivity activity = DiscordActivity.builder()
                    .withImage(discordImageUrl)
                    .withImageText(discordImageText)
                    .withState(discordState)
                    .withDetails(discordDetails)
                    .withStartTimestamp(System.currentTimeMillis())
                    .build();
            FeatherAPI.getMetaService().updateDiscordActivity(player, activity);
        });
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("featherapi")) {
            if (sender.hasPermission("feather.reloadconfig")) {
                reloadConfig();
                loadConfig();
                sender.sendMessage("§aKonfiguracja została przeładowana!");
            } else {
                sender.sendMessage("§cNie masz uprawnień do tej komendy.");
            }
            return true;
        }
        return false;
    }
}

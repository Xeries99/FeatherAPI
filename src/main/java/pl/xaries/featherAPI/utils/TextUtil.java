package pl.xaries.featherAPI.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");

    public TextUtil() {
    }

    public static String format(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        String hexColored;
        while(matcher.find()) {
            hexColored = matcher.group(1).substring(1);
            matcher.appendReplacement(sb, "" + ChatColor.of(hexColored));
        }

        matcher.appendTail(sb);
        hexColored = sb.toString();
        return ChatColor.translateAlternateColorCodes('&', hexColored);
    }

    public static String fixColor(String text) {
        String[] texts = text.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
        StringBuilder finalText = new StringBuilder();

        for(int i = 0; i < texts.length; ++i) {
            if (texts[i].equalsIgnoreCase("&")) {
                ++i;
                if (texts[i].charAt(0) == '#') {
                    finalText.append("" + ChatColor.of(texts[i].substring(0, 7)) + ChatColor.RESET);
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }

        return finalText.toString();
    }

    public static String sendColoredMessage(Player sender, String message) {
        sender.sendMessage(format(message));
        return message;
    }

    public static String sendColoredMessage(CommandSender sender, String message) {
        sender.sendMessage(format(message));
        return message;
    }

    public static String sendColoredTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(format(title), format(subtitle), fadeIn, stay, fadeOut);
        return title;
    }

    public static void actionBarMessage(String message, Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static List<String> fixLore(List<String> lore) {
        ArrayList<String> fixLore = new ArrayList();
        if (lore == null) {
            return fixLore;
        } else {
            lore.forEach((s) -> {
                fixLore.add(fixColor(s));
            });
            return fixLore;
        }
    }
    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(fixColor(message));
    }
}

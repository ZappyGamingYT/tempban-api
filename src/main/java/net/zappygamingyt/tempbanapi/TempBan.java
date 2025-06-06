package net.zappygamingyt.tempbanapi;

import net.zappygamingyt.releaseschecker.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class TempBan extends JavaPlugin {

  private static TempBan tempBanInstance;

  private static File file;
  private static YamlConfiguration cfg;

  private static String dateFormat = "HH:mm:ss dd.MM.yyyy";
  private static String timeZone = "Europe/Warsaw";

  private static String banScreen;

  @Override
  public void onEnable() {
    tempBanInstance = this;
    Plugin plugin = this;
    file = new File("plugins/" + plugin.getName() + "/bans.yml");
    cfg = YamlConfiguration.loadConfiguration(file);
    try {
      if(!file.exists()) {
        file.createNewFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    ReleaseChecker.getVersion("v1.0");
    ReleaseChecker.getRepository("zappygamingyt", "tempban-api");
    if (!ReleaseChecker.releaseCheck()) {
      System.out.println(ChatColor.RED + "\n\nThere is new version of that api! Update now!\n");
    }
  }

  public static void banPlayer(Player player, String reason, long seconds) {
    UUID uuid = player.getUniqueId();
    if (isBanned(String.valueOf(uuid))) {
      return;
    }
    long time = 0;
    long current = System.currentTimeMillis();
    long millis = seconds * 1000;
    time = current + millis;

    if (seconds == -1) {
      time = -1;
    }

    cfg.set("Bans." + uuid + ".time", time);
    cfg.set("Bans." + uuid + ".reason", reason);
    saveBansFile();
  }

  public static void unbanPlayer(Player player) {
    String uuid = String.valueOf(player.getUniqueId());
    cfg.set("Bans." + uuid + ".time", null);
    cfg.set("Bans." + uuid + ".reason", null);
    saveBansFile();
  }

  public static String setBanScreen(String banScreen) {
    return banScreen;
  }

  public static void sendBanScreen(Player player) {
    if (banScreen == null) {
      player.kickPlayer("§cYou have been banned!\n\n" + "§7Reason: §c" + getReason(player.getUniqueId().toString()) + "\n\n" + "§7Unban date: §c" + getUnbanDate(player.getUniqueId().toString()));
      return;
    }
    player.kickPlayer(banScreen.replace("%reason%", getReason(player.getUniqueId().toString())).replace("%unban-date%", getUnbanDate(player.getUniqueId().toString())));
  }

  public static String getReason(Player player) {
    UUID uuid = player.getUniqueId();
    return cfg.getString("Bans." + uuid + ".reason");
  }

  public static String getUnbanDate(Player player) {
    String uuid = String.valueOf(player.getUniqueId());
    long time = cfg.getLong("Bans." + uuid + ".time");
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    Date date = new Date(time);
    return sdf.format(date);
  }

  public static boolean isBanned(Player player) {
    UUID uuid = player.getUniqueId();
    return cfg.getString("Bans." + uuid + ".reason") != null;
  }

  public static long getBanTime(Player player) {
    UUID uuid = player.getUniqueId();
    return cfg.getLong("Bans." + uuid + ".time");
  }

  public static void saveBansFile() {
    try {
      cfg.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String setTimeFormat(String timeZone) {
    return TempBan.timeZone = timeZone;
  }

  public static String setDateFormat(String dateFormat) {
    return TempBan.dateFormat = dateFormat;
  }

  //API METHODS
  private static String getReason(String uuid) {
    return cfg.getString("Bans." + uuid + ".reason");
  }

  private static String getUnbanDate(String uuid) {
    long time = cfg.getLong("Bans." + uuid + ".time");
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    Date date = new Date(time);
    return sdf.format(date);
  }

  private static boolean isBanned(String uuid) {
    return cfg.getString("Bans." + uuid + ".reason") != null;
  }
}

package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class OSUtils {

    private static final Pattern versionPattern = Pattern.compile("[1-9][0-9]{2,}(?:\\.[0-9]+)+");
    private static final Map<OS, OSInfo> OS_INFO_MAP = new EnumMap<>(OS.class);
    private static LooseVersion version = null;
    private static OSInfo info = null;

    // TODO: test windows + macos
    static {
        OS_INFO_MAP.put(OS.LINUX, new OSInfo(
                OS.LINUX,
                "~/.local/share/undetected_chromedriver",
                "chromedriver_linux64.zip",
                new String[]{"/opt/google/chrome/chrome", "--version"}
        ));

        OS_INFO_MAP.put(OS.WINDOWS, new OSInfo(
                OS.WINDOWS,
                "~/appdata/roaming/undetected_chromedriver",
                "chromedriver_win32.zip",
                new String[]{"cmd", "/c", "reg query \"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\" /v version"}
        ));

        OS_INFO_MAP.put(OS.MACOS, new OSInfo(
                OS.MACOS,
                "~/Library/Application Support/undetected_chromedriver",
                "chromedriver_mac64.zip",
                new String[]{"/Application/Google Chrome.app/Contents/MacOS/Google Chrome", "--version"}
        ));
    }

    public static OSInfo getInfo(OS os) {
        return OS_INFO_MAP.get(os);
    }

    public static OSInfo getOS() {
        return getOS(System.getProperty("os.name"));
    }

    public static OSInfo getOS(String name) {
        if (info != null) return info;

        OS os;
        if (name.contains("win")) {
            os = OS.WINDOWS;
        } else if (name.contains("nix") || name.contains("nux") || name.contains("aix")) {
            os = OS.LINUX;
        } else if (name.contains("mac")) {
            os = OS.MACOS;
        } else {
            throw new RuntimeException("couldn't determine operating system");
        }

        info = getInfo(os);

        if (info == null) {
            throw new NullPointerException("couldn't find info for: " + info);
        }

        return info;
    }

    public static LooseVersion getInstalledChromeVersion(String[] command) {
        if (version != null) return version;

        Process proc;
        try {
            proc = new ProcessBuilder(command).start();
        } catch (Exception ex) {
            throw new RuntimeException("Have you installed Google Chrome? " + ex.getMessage());
        }

        Matcher matcher = getMatcher(proc);
        try {
            version = new LooseVersion(matcher.group());
        } catch (Exception ex) {
            throw new RuntimeException("failed to parse version (" + matcher.group() + "): " + ex.getMessage());
        }

        return version;
    }

    private static Matcher getMatcher(Process proc) {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String read;
            while ((read = reader.readLine()) != null) {
                if (read.contains("Google Chrome") || read.contains("version")) {
                    line = read;
                }
            }
            if (line == null) {
                throw new RuntimeException("Couldn't find version.");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        Matcher matcher = versionPattern.matcher(line);
        if (!matcher.find()) {
            throw new RuntimeException("couldn't determine Chrome version");
        }
        return matcher;
    }
}

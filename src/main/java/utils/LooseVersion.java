package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Loose version.
 */
public class LooseVersion implements Comparable<LooseVersion> {
    private static final Pattern COMPONENT_RE = Pattern.compile("(\\d+|[a-z]+|\\.)");
    private final String versionString;
    private final List<Object> version;

    /**
     * Instantiates a new Loose version.
     *
     * @param vString the version stored as a string.
     */
    public LooseVersion(String vString) {
        this.versionString = vString;

        Matcher matcher = COMPONENT_RE.matcher(versionString);
        version = new ArrayList<>();

        while (matcher.find()) {
            String part = matcher.group().trim();
            if (!part.equals(".")) {
                try {
                    version.add(Integer.parseInt(part));
                } catch (NumberFormatException ex) {
                    version.add(part);
                }
            }
        }
    }

    /**
     * Gets part.
     *
     * @param index the location of an object in version
     * @return the Object in version
     */
    public Object getPart(int index) {
        if (index < 0 || index >= version.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return version.get(index);
    }

    public int getParts() {
        return version.size();
    }

    @Override
    public String toString() {
        return this.versionString;
    }

    @Override
    public int compareTo(LooseVersion other) {
        for (int i = 0; i < Math.max(this.version.size(), other.version.size()); i++) {
            Object thisPart = i < this.version.size() ? this.version.get(i) : null;
            Object otherPart = i < other.version.size() ? other.version.get(i) : null;

            int result = compareParts(thisPart, otherPart);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private int compareParts(Object thisPart, Object otherPart) {
        if (thisPart == null) {
            return otherPart == null ? 0 : -1;
        }

        if (otherPart == null) {
            return 1;
        }

        int result;
        if (thisPart instanceof Integer && otherPart instanceof Integer) {
            result = ((Integer) thisPart).compareTo((Integer) otherPart);
        } else if (thisPart instanceof String && otherPart instanceof String) {
            result = ((String) thisPart).compareTo((String) otherPart);
        } else if (thisPart instanceof Integer) { // Integers are less than strings
            result = -1;
        } else {
            result = 1;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        LooseVersion other = (LooseVersion) obj;
        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return versionString != null ? versionString.hashCode() : 0;
    }
}
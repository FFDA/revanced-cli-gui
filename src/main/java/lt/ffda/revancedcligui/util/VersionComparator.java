package lt.ffda.revancedcligui.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        String[] parts1 = extractVersion(o1);
        String[] parts2 = extractVersion(o2);

        if (parts1 == null) {
            return 1;
        }
        if (parts2 == null) {
            return -1;
        }

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (num1 < num2) {
                return 1;
            }
            if (num1 > num2) {
                return -1;
            }
        }

        return 0;
    }

    /**
     * Extracts versions numbers of the resource and returns them as an array
     * @param filename filename of the resource to get the version
     * @return String[] with spilt version number or null if version number could not be extracted
     */
    private String[] extractVersion(String filename) {
        String regex = "(\\d+\\.\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            return matcher.group(1).split("\\.");
        } else {
            return null;
        }
    }


}

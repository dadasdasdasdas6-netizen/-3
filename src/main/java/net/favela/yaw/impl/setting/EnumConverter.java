package net.favela.yaw.impl.setting;

public final class EnumConverter {

    private EnumConverter() {}

    public static String getProperName(Enum<?> value) {
        if (value == null) return "";
        if (value instanceof Displayable displayable) {
            return displayable.getDisplayName();
        }
        return autoFormat(value.name());
    }

    private static String autoFormat(String raw) {
        String name = raw.toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder(name.length());
        boolean cap = true;
        for (char c : name.toCharArray()) {
            if (cap && Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
                cap = false;
            } else {
                sb.append(c);
                if (c == ' ') cap = true;
            }
        }
        return sb.toString();
    }
}
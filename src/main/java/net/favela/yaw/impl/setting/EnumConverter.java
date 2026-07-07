package net.favela.yaw.impl.setting;

public class EnumConverter {

    public static String getProperName(Enum<?> value) {
        if (value == null) return "";
        String name = value.name().toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
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
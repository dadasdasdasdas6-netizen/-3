package net.favela.yaw.impl.util.identifier;

import net.favela.yaw.EntryPoint;
import net.minecraft.resources.Identifier;

public final class Identifiers {

    private Identifiers() {}

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(EntryPoint.name(), path);
    }
}
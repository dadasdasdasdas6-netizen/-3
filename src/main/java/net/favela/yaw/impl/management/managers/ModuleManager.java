package net.favela.yaw.impl.management.managers;

import net.favela.yaw.impl.modules.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<?>, Module> byClass = new HashMap<>();
    private final Map<String, Module> byName = new HashMap<>();

    public void initialize() {
        ServiceLoader.load(Module.class, ModuleManager.class.getClassLoader()).stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt((Module m) -> m.getCategory().ordinal())
                        .thenComparing(Module::getName, String.CASE_INSENSITIVE_ORDER))
                .forEach(this::register);
    }

    public void register(Module module) {
        if (module == null || byClass.containsKey(module.getClass())) return;
        modules.add(module);
        byClass.put(module.getClass(), module);
        byName.put(module.getName().toLowerCase(), module);
    }

    public void unregister(Module module) {
        if (module == null) return;
        modules.remove(module);
        byClass.remove(module.getClass());
        byName.remove(module.getName().toLowerCase());
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public Stream<Module> stream() {
        return modules.stream();
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        return stream().filter(m -> m.getCategory() == category).toList();
    }

    public Module getModuleByName(String name) {
        return name == null ? null : byName.get(name.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) byClass.get(clazz);
    }
}
package com.odtheking.odinaddon.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.Gson;
import com.odtheking.odin.clickgui.settings.Saving;
import com.odtheking.odin.clickgui.settings.Setting;
import com.odtheking.odin.config.ModuleConfig;
import com.odtheking.odin.features.Module;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Makes ModuleConfig saving "cooperative" when multiple addons accidentally (or intentionally) share the same config file.
 *
 * Vanilla ModuleConfig#save() overwrites the file with only the modules registered to that ModuleConfig instance.
 * If multiple addons create their own ModuleConfig pointing at the same file, they will wipe each other's settings.
 *
 * This mixin merges the existing on-disk JSON array with the modules being saved, preserving unknown modules.
 */
@Mixin(ModuleConfig.class)
public abstract class ModuleConfigMixin {

    @Shadow @Final private HashMap<String, Module> modules;
    @Shadow @Final private File file;
    @Shadow @Final private static Gson gson;

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void odinaddon$mergeSave(CallbackInfo ci) {
        // Only affect addon configs (config/odin/addons/*) to avoid changing Odin's own config semantics.
        File parent = file.getParentFile();
        if (parent == null || !"addons".equalsIgnoreCase(parent.getName())) return;

        try {
            // Read existing entries (if any) so we can preserve modules we don't own.
            LinkedHashMap<String, JsonObject> mergedByName = new LinkedHashMap<>();
            ArrayList<String> existingOrder = new ArrayList<>();

            try {
                String existing = Files.readString(file.toPath());
                if (!existing.isBlank()) {
                    JsonElement parsed = JsonParser.parseString(existing);
                    if (parsed.isJsonArray()) {
                        for (JsonElement element : parsed.getAsJsonArray()) {
                            if (element == null || !element.isJsonObject()) continue;
                            JsonObject obj = element.getAsJsonObject();
                            JsonElement nameEl = obj.get("name");
                            if (nameEl == null || !nameEl.isJsonPrimitive() || !nameEl.getAsJsonPrimitive().isString()) continue;
                            String key = nameEl.getAsString().toLowerCase(Locale.ROOT);
                            // Keep first occurrence order; later duplicates will get overwritten by the merge.
                            if (!mergedByName.containsKey(key)) existingOrder.add(key);
                            mergedByName.put(key, obj);
                        }
                    }
                }
            } catch (Exception ignored) {
                // If reading/parsing fails, fall back to writing only our modules below.
                mergedByName.clear();
                existingOrder.clear();
            }

            // Overwrite/append our modules with current in-memory values.
            for (Module module : modules.values()) {
                JsonObject moduleObj = new JsonObject();
                moduleObj.add("name", new JsonPrimitive(module.getName()));
                moduleObj.add("enabled", new JsonPrimitive(module.getEnabled()));

                JsonObject settingsObj = new JsonObject();
                for (Map.Entry<String, Setting<?>> entry : module.getSettings().entrySet()) {
                    Setting<?> setting = entry.getValue();
                    if (setting instanceof Saving saving) {
                        settingsObj.add(entry.getKey(), saving.write());
                    }
                }
                moduleObj.add("settings", settingsObj);

                String key = module.getName().toLowerCase(Locale.ROOT);
                if (!mergedByName.containsKey(key)) existingOrder.add(key);
                mergedByName.put(key, moduleObj);
            }

            // Build the final array, keeping original ordering where possible.
            JsonArray out = new JsonArray();
            Set<String> written = new HashSet<>();

            for (String key : existingOrder) {
                JsonObject obj = mergedByName.get(key);
                if (obj == null) continue;
                out.add(obj);
                written.add(key);
            }

            // Safety: append anything not accounted for in existingOrder.
            for (Map.Entry<String, JsonObject> entry : mergedByName.entrySet()) {
                if (written.contains(entry.getKey())) continue;
                out.add(entry.getValue());
            }

            Files.writeString(file.toPath(), gson.toJson(out), StandardCharsets.UTF_8);
            ci.cancel();
        } catch (Exception e) {
            // Let the original save() run as a fallback.
        }
    }
}


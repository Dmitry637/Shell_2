package ru.example.dependencygraph.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Минимальный INI-парсер без сторонних библиотек. */
public final class IniConfigLoader {
    private static final Set<String> ALLOWED_KEYS = Set.of(
            "package_name",
            "repository_mode",
            "repository",
            "package_version",
            "graph_output_file"
    );

    private IniConfigLoader() {
    }

    public static IniLoadResult load(Path file) {
        Map<String, String> values = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        Path absoluteFile = file.toAbsolutePath().normalize();

        if (!Files.exists(absoluteFile)) {
            errors.add("INI-файл не найден: " + absoluteFile);
            return new IniLoadResult(values, errors);
        }
        if (!Files.isRegularFile(absoluteFile) || !Files.isReadable(absoluteFile)) {
            errors.add("INI-путь должен указывать на доступный для чтения файл: " + absoluteFile);
            return new IniLoadResult(values, errors);
        }

        boolean inAppSection = false;
        boolean appSectionFound = false;
        List<String> lines;
        try {
            lines = Files.readAllLines(absoluteFile, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            errors.add("Не удалось прочитать INI-файл: " + exception.getMessage());
            return new IniLoadResult(values, errors);
        }

        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String line = lines.get(index).trim();
            if (line.isEmpty() || line.startsWith(";") || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                String section = line.substring(1, line.length() - 1).trim();
                inAppSection = "app".equals(section);
                if (inAppSection) {
                    appSectionFound = true;
                } else {
                    errors.add("Строка " + lineNumber + ": неизвестная секция [" + section + "]; ожидается [app].");
                }
                continue;
            }

            if (!inAppSection) {
                errors.add("Строка " + lineNumber + ": параметр должен находиться внутри секции [app].");
                continue;
            }

            int separator = line.indexOf('=');
            if (separator < 1) {
                errors.add("Строка " + lineNumber + ": ожидалась запись key = value.");
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            if (!ALLOWED_KEYS.contains(key)) {
                errors.add("Строка " + lineNumber + ": неизвестный параметр '" + key + "'.");
                continue;
            }
            if (values.containsKey(key)) {
                errors.add("Строка " + lineNumber + ": параметр '" + key + "' указан повторно.");
                continue;
            }
            values.put(key, value);
        }

        if (!appSectionFound) {
            errors.add("В INI-файле отсутствует секция [app].");
        }
        return new IniLoadResult(values, errors);
    }
}

package ru.example.dependencygraph.config;

import java.util.List;
import java.util.Map;

/** Результат разбора INI: найденные значения и ошибки синтаксиса. */
public record IniLoadResult(Map<String, String> values, List<String> errors) {
    public IniLoadResult {
        values = Map.copyOf(values);
        errors = List.copyOf(errors);
    }
}

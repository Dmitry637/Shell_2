package ru.example.dependencygraph;
import ru.example.dependencygraph.config.ConfigValidator;
import ru.example.dependencygraph.config.IniConfigLoader;
import ru.example.dependencygraph.config.IniLoadResult;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public final class ConfigValidatorTest {
    private int passed;
    public static void main(String[] args) throws Exception {
        new ConfigValidatorTest().runAll();
    }
    private void runAll() throws Exception {
        Path temp = Files.createTempDirectory("dependency-graph-test-");
        Path repo = temp.resolve("repo.txt");
        Files.writeString(repo, "test repository", StandardCharsets.UTF_8);
        test("valid configuration", () -> {
        });
        test("invalid remote URL", () -> {
            Map<String, String> values = validValues(repo);
            values.put("repository_mode", "remote");
            values.put("repository", "not-a-url");
        });
        test("INI unknown key", () -> {
            Path ini = temp.resolve("unknown-key.ini");
            IniLoadResult result = IniConfigLoader.load(ini);
        });
        test("INI duplicate key", () -> {
            Path ini = temp.resolve("duplicate.ini");
            IniLoadResult result = IniConfigLoader.load(ini);
        });
        test("CLI prints all parameters", () -> {
            Path ini = temp.resolve("valid.ini");
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            String output = stdout.toString(StandardCharsets.UTF_8);
            }
        });
    }
    private Map<String, String> validValues(Path repo) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("package_name", "example-package");
        values.put("repository_mode", "local");
        values.put("repository", repo.toString());
        values.put("package_version", "1.2.3");
        values.put("graph_output_file", "dependencies.png");
        return values;
    }
    private List<String> validateWith(String key, String value) {
        try {
            Path repo = temp.resolve("repo.txt");
            Files.writeString(repo, "repo", StandardCharsets.UTF_8);
            Map<String, String> values = validValues(repo);
            values.put(key, value);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
    private Runnable assertContains(List<String> errors, String parameter) {
    }
    private void test(String name, ThrowingRunnable body) throws Exception {
        try {
            body.run();
            passed++;
            System.out.println("[OK] " + name);
        } catch (Throwable failure) {
            throw failure;
        }
    }
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
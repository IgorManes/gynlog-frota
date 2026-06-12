package br.com.gynlog.ui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ThemeCss {
    private static final String RESOURCE = "/styles/gynlog-theme.css";
    private final Map<String, String> values = new HashMap<>();

    private ThemeCss() {
    }

    public static ThemeCss load() {
        ThemeCss css = new ThemeCss();
        try (InputStream stream = ThemeCss.class.getResourceAsStream(RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Tema CSS nao encontrado: " + RESOURCE);
            }
            css.read(stream);
            return css;
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel carregar o tema CSS.", exception);
        }
    }

    public Color color(String name) {
        String value = required(name);
        return Color.decode(value);
    }

    public int number(String name) {
        return Integer.parseInt(required(name));
    }

    public String text(String name) {
        return required(name);
    }

    private void read(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(String::trim)
                    .filter(line -> line.startsWith("--") && line.contains(":"))
                    .forEach(this::parseVariable);
        }
    }

    private void parseVariable(String line) {
        int separator = line.indexOf(':');
        String name = line.substring(2, separator).trim();
        String value = line.substring(separator + 1).replace(";", "").trim();
        values.put(name, value);
    }

    private String required(String name) {
        String value = values.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Variavel CSS nao encontrada: --" + name);
        }
        return value;
    }
}

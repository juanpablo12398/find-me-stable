package edu.utn.proyecto.common.security;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class HtmlSanitizer {

    private static final Pattern SCRIPT_TAG =
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HTML_TAG =
            Pattern.compile("<[^>]+>");

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String clean = SCRIPT_TAG.matcher(input).replaceAll("");
        clean = HTML_TAG.matcher(clean).replaceAll("");
        return clean.trim();
    }
}
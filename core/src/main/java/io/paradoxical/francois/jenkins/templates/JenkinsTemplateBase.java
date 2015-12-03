package io.paradoxical.francois.jenkins.templates;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import javaslang.Lazy;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JenkinsTemplateBase {

    protected static final Pattern parameterPattern =
            Pattern.compile("\\{%\\s*([\\w\\.\\-]+)\\s*(?:\\|\\s*(.*?)\\s*)?%\\}");

    @NonNull
    @Getter
    private final String templateName;

    @NonNull
    @Getter
    private final String templateDefinition;

    @JsonIgnore
    private final Lazy<HashMap<String, String>> templateParameters = Lazy.of(this::getTemplateParameterMap);

    @JsonGetter
    public HashMap<String, String> getParameters() {
        return templateParameters.get();
    }

    protected JenkinsTemplateBase(final String templateName, final String templateDefinition) {
        this.templateName = templateName;
        this.templateDefinition = templateDefinition;
    }

    protected HashMap<String, String> getTemplateParameterMap() {
        final Matcher matcher = parameterPattern.matcher(getTemplateDefinition());

        final HashMap<String, String> paramMap = Maps.newHashMap();

        while (matcher.find()) {

            final String name = matcher.group(1);
            final String defaultValue = matcher.group(2);

            paramMap.compute(name, (k, existingValue) -> coallesceValues(existingValue, defaultValue));
        }

        return paramMap;
    }

    private String coallesceValues(
            final String first,
            final String second) {
        return !Strings.isNullOrEmpty(first) ? first : Strings.nullToEmpty(second);
    }

    public String apply(Map<String, String> parameters) {

        final Map<String, String> defaultParams = getTemplateParameterMap();

        final Matcher matcher = parameterPattern.matcher(getTemplateDefinition());
        final StringBuffer configBuffer = new StringBuffer();

        while (matcher.find()) {

            final String paramName = matcher.group(1);
            final String suppliedValue = parameters.getOrDefault(paramName, null);

            final String value = coallesceValues(suppliedValue, defaultParams.getOrDefault(paramName, ""));

            matcher.appendReplacement(configBuffer, Strings.nullToEmpty(value));
        }

        matcher.appendTail(configBuffer);


        final String configXml = configBuffer.toString();

        return configXml;
    }

}

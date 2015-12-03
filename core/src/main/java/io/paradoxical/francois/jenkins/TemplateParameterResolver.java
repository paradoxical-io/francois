package io.paradoxical.francois.jenkins;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.paradoxical.francois.jenkins.templates.JenkinsTemplateBase;
import io.paradoxical.francois.jenkins.templates.JobParameterValue;
import io.paradoxical.francois.jenkins.templates.JobTemplate;
import io.paradoxical.francois.jenkins.templates.PromotionTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toMap;

public class TemplateParameterResolver {
    public HashMap<String, String> resolveDefaultParameters(
            final JobTemplate jobTemplate,
            final List<PromotionTemplate> promotions)
            throws IOException {

        final HashMap<String, String> parametersMap = Maps.newHashMap(jobTemplate.getParameters());


        final HashMap<String, String> resolvedParams = promotions.stream()
                                                                 .map(JenkinsTemplateBase::getParameters)
                                                                 .reduce(parametersMap, this::mergeDefaultParameters);


        return resolvedParams;
    }

    public HashMap<String, String> fullyResolveTemplateApplicationParameters(
            final List<JobParameterValue> parameterValues,
            final JobTemplate jobTemplate,
            final List<PromotionTemplate> promotions) throws IOException {
        final HashMap<String, String> resolvedDefaultParameters = resolveDefaultParameters(jobTemplate, promotions);

        final Map<String, String> suppliedParams = parameterValues.stream().collect(toMap(JobParameterValue::getName, JobParameterValue::getValue));

        return mergeDefaultParameters(suppliedParams, resolvedDefaultParameters);
    }

    private HashMap<String, String> mergeDefaultParameters(
            final Map<String, String> baseParametersMap,
            final Map<String, String> defaultsToMerge) {

        final HashMap<String, String> resolvedParameters = Maps.newHashMap(baseParametersMap);

        final BiConsumer<String, String> parameterSelector =
                (k, defaultValue) ->
                        resolvedParameters.compute(k, (existingKey, existingValue) -> selectValue(defaultValue, existingValue));

        defaultsToMerge.forEach(parameterSelector);

        return resolvedParameters;
    }

    private String selectValue(String defaultValue, String existingValue) {
        return Strings.isNullOrEmpty(existingValue) ? Strings.nullToEmpty(defaultValue) : existingValue;
    }
}

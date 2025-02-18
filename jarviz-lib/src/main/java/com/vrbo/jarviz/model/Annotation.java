package com.vrbo.jarviz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAnnotation.class)
@JsonDeserialize(as = ImmutableAnnotation.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface Annotation {
    String getAnnotationTarget();

    String getAnnotationName();

    class Builder extends ImmutableAnnotation.Builder {}
}

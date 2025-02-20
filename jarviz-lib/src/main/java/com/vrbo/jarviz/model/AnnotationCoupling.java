package com.vrbo.jarviz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAnnotationCoupling.class)
@JsonDeserialize(as = ImmutableAnnotationCoupling.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface AnnotationCoupling {

    Annotation getSource();

    ShadowClass getShadowClass();

    class Builder extends ImmutableAnnotationCoupling.Builder {}
}

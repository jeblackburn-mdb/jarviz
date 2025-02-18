package com.vrbo.jarviz.service;

import com.vrbo.jarviz.config.CouplingFilterConfig;
import com.vrbo.jarviz.model.Annotation;
import com.vrbo.jarviz.model.CouplingFilter;
import com.vrbo.jarviz.model.MethodCoupling;
import com.vrbo.jarviz.visitor.FilteredClassVisitor;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilteredClassVisitorTest {
    @Test
    public void testReadsAnnotations() throws Exception {
        UsageCollector collector = new UsageCollector();
        FilteredClassVisitor visitor = new FilteredClassVisitor("com.vrbo.jarviz.model.ApplicationSet", collector);
        visitor.visit();
        List<MethodCoupling> methodCouplings = collector.getMethodCouplings();
        assertThat(methodCouplings).isNotEmpty();
        List<Annotation> annotationCouplings = collector.getAnnotationCouplings();
        assertThat(annotationCouplings).hasSize(4);
    }

    @Test
    public void testHonorsPackageFilter() throws Exception {
        CouplingFilterConfig config = new CouplingFilterConfig.Builder()
            .exclude(new CouplingFilter.Builder().targetPackage("^(org\\.immutables).*$").build())
            .build();
        UsageCollector collector = new UsageCollector(config);
        FilteredClassVisitor visitor = new FilteredClassVisitor("com.vrbo.jarviz.model.ApplicationSet", collector);
        visitor.visit();
        List<MethodCoupling> methodCouplings = collector.getMethodCouplings();
        assertThat(methodCouplings).isNotEmpty();
        List<Annotation> annotationCouplings = collector.getAnnotationCouplings();
        assertThat(annotationCouplings.size()).isEqualTo(3);
    }

    @Test
    public void testProperlyFormatsAnnotationClassName() throws Exception {
        UsageCollector collector = new UsageCollector();
        FilteredClassVisitor visitor = new FilteredClassVisitor("com.vrbo.jarviz.model.ApplicationSet", collector);
        visitor.visit();
        List<MethodCoupling> methodCouplings = collector.getMethodCouplings();
        assertThat(methodCouplings).isNotEmpty();
        List<Annotation> annotationCouplings = collector.getAnnotationCouplings();
        assertThat(annotationCouplings.get(0).getAnnotationName()).doesNotContain("/");
        assertThat(annotationCouplings.get(0).getAnnotationName()).doesNotContain(";");
        assertThat(annotationCouplings.get(0).getAnnotationName()).doesNotStartWith("L");
    }
}

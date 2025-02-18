/*
 * Copyright 2020 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vrbo.jarviz.visitor;

import java.io.IOException;
import java.util.Objects;

import com.vrbo.jarviz.model.*;
import org.objectweb.asm.*;

import static com.vrbo.jarviz.util.NamingUtils.toSourceCodeFormat;
import static com.vrbo.jarviz.visitor.FilteredMethodVisitor.cleanseClassName;
import static org.objectweb.asm.Opcodes.ASM7;

public class FilteredClassVisitor extends ClassVisitor {

    private final String className;

    private final ClassReader reader;

    private final Collector collect;

    public FilteredClassVisitor(final String className, final Collector collect) throws IOException {
        this(className, collect, new ClassReader(className));
    }

    public FilteredClassVisitor(final String className, final Collector collect, final byte[] classData) {
        this(className, collect, new ClassReader(classData));
    }

    private FilteredClassVisitor(final String className, final Collector collect, final ClassReader classReader) {
        super(ASM7);

        Objects.requireNonNull(className);
        Objects.requireNonNull(collect);

        this.className = cleanseClassName(toSourceCodeFormat(className));
        this.collect = collect;

        this.reader = classReader;
    }

    /**
     * This will scan this class and visit all the method contents to scan for dependencies.
     */
    public void visit() {
        reader.accept(this, 0);
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String descriptor,
                                     final String signature,
                                     final String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        final Method method = new Method.Builder()
            .className(className)
            .methodName(name)
            .build();
        return new FilteredMethodVisitor(method, methodVisitor, collect);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        // descriptor looks like "Lcom.foo.bar.Annotation"
        Annotation annotation = new Annotation.Builder()
            .annotationTarget(this.className)
            .annotationName(cleanUpAnnotationName(descriptor))
            .build();
        collect.collectAnnotation(annotation);

        return super.visitAnnotation(descriptor, visible);
    }

    private static String cleanUpAnnotationName(String descriptor) {
        return
            cleanseClassName(
                toSourceCodeFormat(
                    descriptor.substring(1)
                        .replace(";", "")));
    }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.resources;

import static org.jboss.weld.util.cache.LoadingCacheUtils.getCacheValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.NormalScope;
import javax.inject.Scope;

import org.jboss.weld.bootstrap.api.helpers.AbstractBootstrapService;
import org.jboss.weld.metadata.TypeStore;
import org.jboss.weld.util.Annotations;
import org.jboss.weld.util.reflection.Reflections;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

public class DefaultReflectionCache extends AbstractBootstrapService implements ReflectionCache {

    private final TypeStore store;
    private final CacheLoader<AnnotatedElement, Set<Annotation>> ANNOTATIONS_FUNCTION = new CacheLoader<AnnotatedElement, Set<Annotation>>() {
        @Override
        public Set<Annotation> load(AnnotatedElement input) {
            return ImmutableSet.copyOf(internalGetAnnotations(input));
        }
    };
    private final CacheLoader<AnnotatedElement, Set<Annotation>> DECLARED_ANNOTATIONS_FUNCTION = new CacheLoader<AnnotatedElement, Set<Annotation>>() {
        @Override
        public Set<Annotation> load(AnnotatedElement input) {
            return ImmutableSet.copyOf(internalGetDeclaredAnnotations(input));
        }
    };

    protected Annotation[] internalGetAnnotations(AnnotatedElement element) {
        return element.getAnnotations();
    }

    protected Annotation[] internalGetDeclaredAnnotations(AnnotatedElement element) {
        return element.getDeclaredAnnotations();
    }

    private final LoadingCache<AnnotatedElement, Set<Annotation>> annotations;
    private final LoadingCache<AnnotatedElement, Set<Annotation>> declaredAnnotations;
    private final LoadingCache<Class<?>, Set<Annotation>> backedAnnotatedTypeAnnotations;
    private final LoadingCache<Class<? extends Annotation>, AnnotationClass<?>> annotationClasses;

    public DefaultReflectionCache(TypeStore store) {
        this.store = store;
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        this.annotations = cacheBuilder.build(ANNOTATIONS_FUNCTION);
        this.declaredAnnotations = cacheBuilder.build(DECLARED_ANNOTATIONS_FUNCTION);
        this.backedAnnotatedTypeAnnotations = cacheBuilder.build(new BackedAnnotatedTypeAnnotationsFunction());
        this.annotationClasses =  cacheBuilder.build(new AnnotationClassFunction());
    }

    @Override
    public void cleanupAfterBoot() {
        annotations.invalidateAll();
        declaredAnnotations.invalidateAll();
        backedAnnotatedTypeAnnotations.invalidateAll();
        annotationClasses.invalidateAll();
    }

    @Override
    public Set<Annotation> getAnnotations(AnnotatedElement element) {
        return getCacheValue(annotations, element);
    }

    @Override
    public Set<Annotation> getDeclaredAnnotations(AnnotatedElement element) {
        return getCacheValue(declaredAnnotations, element);
    }

    @Override
    public Set<Annotation> getBackedAnnotatedTypeAnnotationSet(Class<?> javaClass) {
        return getCacheValue(backedAnnotatedTypeAnnotations, javaClass);
    }

    private class BackedAnnotatedTypeAnnotationsFunction extends CacheLoader<Class<?>, Set<Annotation>> {

        @Override
        public Set<Annotation> load(Class<?> javaClass) {
            Set<Annotation> annotations = getAnnotations(javaClass);
            boolean scopeFound = false;
            for (Annotation annotation : annotations) {
                boolean isScope = getAnnotationClass(annotation.annotationType()).isScope();
                if (isScope && scopeFound) {
                    // there are at least two scopes, we need to choose one using scope inheritance rules (4.1)
                    return applyScopeInheritanceRules(annotations, javaClass);
                }
                if (isScope) {
                    scopeFound = true;
                }
            }
            return annotations;
        }

        public Set<Annotation> applyScopeInheritanceRules(Set<Annotation> annotations, Class<?> javaClass) {
            Set<Annotation> result = new HashSet<Annotation>();
            for (Annotation annotation : annotations) {
                if (!getAnnotationClass(annotation.annotationType()).isScope()) {
                    result.add(annotation);
                }
            }
            result.addAll(findTopLevelScopeDefinitions(javaClass));
            return ImmutableSet.copyOf(result);
        }

        public Set<Annotation> findTopLevelScopeDefinitions(Class<?> javaClass) {
            for (Class<?> clazz = javaClass; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
                Set<Annotation> scopes = new HashSet<Annotation>();
                for (Annotation annotation : getDeclaredAnnotations(clazz)) {
                    if (getAnnotationClass(annotation.annotationType()).isScope()) {
                        scopes.add(annotation);
                    }
                }
                if (scopes.size() > 0) {
                    return scopes;
                }
            }
            throw new IllegalStateException();
        }
    }

    private class AnnotationClassFunction extends CacheLoader<Class<? extends Annotation>, AnnotationClass<?>> {
        @Override
        public AnnotationClass<?> load(Class<? extends Annotation> input) {
            boolean scope = input.isAnnotationPresent(NormalScope.class) || input.isAnnotationPresent(Scope.class) || store.isExtraScope(input);
            Method repeatableAnnotationAccessor = Annotations.getRepeatableAnnotationAccessor(input);
            Set<Annotation> metaAnnotations = ImmutableSet.copyOf(internalGetAnnotations(input));
            return new AnnotationClassImpl<>(scope, repeatableAnnotationAccessor, metaAnnotations);
        }
    }

    private static class AnnotationClassImpl<T extends Annotation> implements AnnotationClass<T> {

        private final boolean scope;
        private final Method repeatableAnnotationAccessor;
        private final Set<Annotation> metaAnnotations;

        public AnnotationClassImpl(boolean scope, Method repeatableAnnotationAccessor, Set<Annotation> metaAnnotations) {
            this.scope = scope;
            this.repeatableAnnotationAccessor = repeatableAnnotationAccessor;
            this.metaAnnotations = metaAnnotations;
        }

        @Override
        public Set<Annotation> getMetaAnnotations() {
            return metaAnnotations;
        }

        @Override
        public boolean isScope() {
            return scope;
        }

        @Override
        public boolean isRepeatableAnnotationContainer() {
            return repeatableAnnotationAccessor != null;
        }

        @Override
        public Annotation[] getRepeatableAnnotations(Annotation annotation) {
            if (!isRepeatableAnnotationContainer()) {
                throw new IllegalStateException("Not a repeatable annotation container " + annotation);
            }
            try {
                return (Annotation[]) repeatableAnnotationAccessor.invoke(annotation);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Error reading repeatable annotations on " + annotation.annotationType(), e);
            }
        }
    }

    @Override
    public <T extends Annotation> AnnotationClass<T> getAnnotationClass(Class<T> clazz) {
        return Reflections.cast(getCacheValue(annotationClasses, clazz));
    }
}

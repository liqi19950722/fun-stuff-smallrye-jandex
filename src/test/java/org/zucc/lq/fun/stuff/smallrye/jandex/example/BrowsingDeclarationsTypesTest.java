package org.zucc.lq.fun.stuff.smallrye.jandex.example;

import org.jboss.jandex.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;

public class BrowsingDeclarationsTypesTest {

    @Test
    public void browsingClass() throws IOException {
        var indexer = Index.of(Map.class);

        var clazz = indexer.getClassByName(DotName.createSimple("java.util.Map"));

        clazz.methods().forEach(System.out::println);
    }

    @Test
    public void searchFroAnnotations() throws IOException {

        var indexer = Index.of(Thread.class, String.class);

        indexer.getAnnotations(DotName.createSimple(Deprecated.class))
                .stream()
                .filter(annotation -> Objects.equals(annotation.target().kind(), AnnotationTarget.Kind.METHOD))
                .forEach(annotation -> System.out.printf("%s %s\n", annotation.target(), annotation));
    }

    @Test
    public void analyzeGeneric() throws IOException {
        var indexer = Index.of(Collections.class);

        var clazzInfo = indexer.getClassByName(DotName.createSimple(Collections.class));

        var listType = Type.create(DotName.createSimple(List.class), Type.Kind.CLASS);

        var sortMethod = clazzInfo.method("sort", listType);

        var parameterTypes = sortMethod.parameterTypes().get(0).asParameterizedType();
        System.out.println(parameterTypes);
        System.out.println(parameterTypes.arguments().get(0).asTypeVariable());

        var t = parameterTypes.arguments().get(0).asTypeVariable().bounds().get(0).asParameterizedType();

        System.out.println(t.asParameterizedType());
        System.out.println(t.asParameterizedType().arguments().get(0).asWildcardType());
        System.out.println(t.asParameterizedType().arguments().get(0).asWildcardType().superBound());

    }

    Map<Integer, List<@Label("name") String>> names;

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({TYPE_USE})
    public @interface Label {
        String value() default "";
    }

    @Test
    public void browsingTypeAnnotations() throws IOException {
        var indexer = Index.of(BrowsingDeclarationsTypesTest.class, Label.class);
        var field = indexer.getClassByName(BrowsingDeclarationsTypesTest.class).field("names");

        var type = field.type().asParameterizedType();
        System.out.println(type);

        type.arguments().forEach(System.out::println);

        var parameterizedType = type.arguments().get(1).asParameterizedType();
        System.out.println(parameterizedType.arguments().get(0));

        System.out.println(parameterizedType.arguments().get(0).annotations().get(0));
        System.out.println(parameterizedType.arguments().get(0).annotations().get(0).value().asString());
    }

    @Test
    public void searchForTypeAnnotations() throws IOException {
        var indexer = Index.of(BrowsingDeclarationsTypesTest.class, Label.class);

        var annotations = indexer.getAnnotations(DotName.createSimple(Label.class));

        annotations.forEach(annotation -> {
            if (annotation.target().kind() == AnnotationTarget.Kind.TYPE) {
                var typeTarget = annotation.target().asType();
                System.out.println("Type usage is located within: " + typeTarget.enclosingTarget());
                System.out.println("Usage type: " + typeTarget.usage());

                System.out.println("Target type: " + typeTarget.target());
                System.out.println("Expected target? " + (typeTarget.enclosingTarget().asField().type()
                        .asParameterizedType().arguments().get(1)
                        .asParameterizedType().arguments().get(0)
                        == typeTarget.target()));
            }
        });

    }
}

/*
 *
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.example.demo;


import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.Optional;

import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam;


@Component
@Order(0)
public class IdConverterDataTypeReader implements ParameterBuilderPlugin {

    private final TypeNameExtractor nameExtractor;

    private final TypeResolver resolver;


    @Autowired
    public IdConverterDataTypeReader(TypeNameExtractor nameExtractor, TypeResolver resolver) {
        this.nameExtractor = nameExtractor;
        this.resolver = resolver;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(ParameterContext context) {
        ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
        ResolvedType parameterType = methodParameter.getParameterType();
        parameterType = context.alternateFor(parameterType);
        ModelReference modelRef = null;

        /**
         * For simplicity, this only acts on Payment.class. In our real application we have hundreds of entity classes
         * with a single superclass AbstractEntity, and I check here if parameterType.isInstanceOf(AbstractEntity.class)
         * Annotating every single occurrence or class is not possible.
         */
        if ((methodParameter.findAnnotation(RequestParam.class).isPresent()
                || methodParameter.findAnnotation(PathVariable.class).isPresent())
                && parameterType.isInstanceOf(Payment.class)) {
            parameterType = resolver.resolve(Long.class);
            modelRef = new ModelRef("long");
        }

        ModelContext modelContext = inputParam(context.getGroupName(),
                parameterType,
                context.getDocumentationType(),
                context.getAlternateTypeProvider(),
                context.getGenericNamingStrategy(),
                context.getIgnorableParameterTypes());
        context.parameterBuilder()
                .type(parameterType)
                .modelRef(Optional.ofNullable(modelRef)
                        .orElse(modelRefFactory(modelContext, nameExtractor).apply(parameterType)));
    }
}

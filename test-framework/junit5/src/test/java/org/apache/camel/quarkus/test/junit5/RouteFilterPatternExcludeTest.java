/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.test.junit5;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.Model;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(RouteFilterPatternExcludeTest.class)
public class RouteFilterPatternExcludeTest extends CamelQuarkusTestSupport {

    @Override
    public String getRouteFilterExcludePattern() {
        return "bar*";
    }

    @Test
    public void testRouteFilter() throws Exception {
        assertEquals(1, context.getRoutes().size());
        assertEquals(1, context.getExtension(Model.class).getRouteDefinitions().size());
        assertEquals("foo", context.getExtension(Model.class).getRouteDefinitions().get(0).getId());

        getMockEndpoint("mock:foo").expectedMessageCount(1);

        template.sendBody("direct:foo", "Hello World");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:foo").routeId("foo").to("mock:foo");

                from("direct:bar").routeId("bar").to("mock:bar");
            }
        };
    }

}

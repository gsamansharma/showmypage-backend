package page.showmy.config;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import page.showmy.security.ServerToServerAuthenticationToken;

import graphql.schema.FieldCoordinates;

@Configuration
public class GraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.directive("Auth", new SchemaDirectiveWiring() {
            @Override
            public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
                GraphQLFieldDefinition field = env.getElement();
                FieldCoordinates coordinates = FieldCoordinates.coordinates(env.getFieldsContainer().getName(), field.getName());
                DataFetcher<?> originalDataFetcher = env.getCodeRegistry().getDataFetcher(coordinates, field);

                DataFetcher<?> authDataFetcher = dataFetchingEnvironment -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth == null || !auth.isAuthenticated() || auth instanceof ServerToServerAuthenticationToken) {
                        throw new AccessDeniedException("Unauthorized: Valid JWT required");
                    }
                    return originalDataFetcher.get(dataFetchingEnvironment);
                };

                env.getCodeRegistry().dataFetcher(coordinates, authDataFetcher);
                return field;
            }
        });
    }
}

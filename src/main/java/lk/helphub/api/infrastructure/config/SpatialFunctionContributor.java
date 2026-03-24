package lk.helphub.api.infrastructure.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.NamedSqmFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.type.StandardBasicTypes;

public class SpatialFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        var typeConfiguration = functionContributions.getTypeConfiguration();
        var basicTypeRegistry = typeConfiguration.getBasicTypeRegistry();
        var booleanType = basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN);

        functionContributions.getFunctionRegistry().register(
                "dwithin",
                new NamedSqmFunctionDescriptor(
                        "ST_DWithin",
                        true,
                        null,
                        StandardFunctionReturnTypeResolvers.invariant(
                                typeConfiguration.getBasicTypeRegistry()
                                        .resolve(StandardBasicTypes.BOOLEAN)
                        ),
                        null
                )
        );
    }
}

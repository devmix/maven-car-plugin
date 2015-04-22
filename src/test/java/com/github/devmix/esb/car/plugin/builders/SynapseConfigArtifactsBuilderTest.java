package com.github.devmix.esb.car.plugin.builders;

import com.github.devmix.esb.car.plugin.Constants;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Sergey Grachev
 */
public final class SynapseConfigArtifactsBuilderTest {

    public static void main(String[] args) throws MojoFailureException {
        final ArtifactsListBuilder artifactsListBuilder = new ArtifactsListBuilder();
        final String configDir = SynapseConfigArtifactsBuilderTest.class.getResource("/synapse-config")
                .getFile().substring(1);

        SynapseConfigArtifactsBuilder.newInstance()
                .artifactsList(artifactsListBuilder)
                .outputDirectory("F:\\wso2\\maven-car-plugin\\out")
                .configDir(configDir)
                .serverRole(Constants.SERVER_ROLE_ENTERPRISE_SERVICE_BUS)
                .version("1.0.0")
                .build();

        System.out.println(artifactsListBuilder);
    }
}

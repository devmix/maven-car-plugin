package com.github.devmix.esb.car.plugin.builders;

import com.github.devmix.esb.car.plugin.Constants;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Sergey Grachev
 */
public final class RegistryArtifactsBuilderTest {

    public static void main(final String[] args) throws MojoFailureException {
        final ArtifactsListBuilder artifactsListBuilder = new ArtifactsListBuilder();
        final String configDir = RegistryArtifactsBuilderTest.class.getResource("/registry")
                .getFile()/*.substring(1)*/;

        RegistryArtifactsBuilder.newInstance()
                .artifactsList(artifactsListBuilder)
                .outputDirectory("/mnt/disks/work/projects/esb/maven-car-plugin/out")
                .configDir(configDir)
                .serverRole(Constants.SERVER_ROLE_ENTERPRISE_SERVICE_BUS)
                .version("1.0.0")
                .allInOne(true).allInOneName("res")
                .build();

        System.out.println(artifactsListBuilder);
    }
}

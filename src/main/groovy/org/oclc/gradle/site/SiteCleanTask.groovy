package org.oclc.gradle.site

import org.apache.commons.io.FileUtils
import org.apache.ivy.core.IvyContext
import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.descriptor.DefaultArtifact
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.artifacts.DefaultArtifactIdentifier
import org.gradle.api.internal.artifacts.DependencyManagementServices
import org.gradle.api.internal.artifacts.ivyservice.DefaultBuildableArtifactResolveResult
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.IvyAwareModuleVersionRepository
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.IvyContextualiser
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.IvyDependencyResolverAdapter
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver
import org.gradle.api.internal.artifacts.repositories.resolver.MavenResolver
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.logging.ProgressLoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
class SiteCleanTask extends DefaultTask{

    File outputDirectory = project.file(SitePluginExtension.DEFAULT_OUTPUT_DIRECTORY)

    @TaskAction
    def clean() {


        FileUtils.deleteDirectory(outputDirectory)
    }

}

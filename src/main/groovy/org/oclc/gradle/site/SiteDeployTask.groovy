package org.oclc.gradle.site

import org.apache.maven.artifact.manager.WagonManager
import org.apache.maven.wagon.TransferFailedException
import org.apache.maven.wagon.UnsupportedProtocolException
import org.apache.maven.wagon.Wagon
import org.apache.maven.wagon.authentication.AuthenticationInfo
import org.apache.maven.wagon.observers.Debug
import org.apache.maven.wagon.proxy.ProxyInfo
import org.apache.maven.wagon.repository.Repository
import org.codehaus.plexus.*
import org.codehaus.plexus.component.repository.exception.ComponentLookupException
import org.codehaus.plexus.util.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.mvn3.org.apache.maven.plugin.MojoExecutionException
import org.oclc.gradle.doxia.tools.SiteTool

import javax.naming.OperationNotSupportedException

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/7/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteDeployTask extends DefaultTask {

    @InputDirectory
    File inputDirectory = getProject().file(SitePluginExtension.DEFAULT_OUTPUT_DIRECTORY)

    @Input
    String url

    private SiteTool siteTool = new SiteTool()

    @TaskAction
    def delopy() {
        if (url != null && !url.startsWith("file:")) {
            throw new Exception("Only support local file in site-deploy")
        }

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer(containerConfiguration);

        WagonManager wagonManager = container.lookup(WagonManager.ROLE)
        Repository repository = new Repository("id", url)

        Wagon wagon =  wagonManager.getWagon(repository)
        String relativeDir = getDeployModuleDirectory()

        wagon.connect( repository );

        wagon.putDirectory(inputDirectory, relativeDir)
    }

    def String getDeployModuleDirectory() {
        String relative = siteTool.getRelativePath( project.projectDir.toString(), project.rootDir.toString())
        return ( "".equals( relative ) ) ? "./" : relative;
    }
}

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

    SiteTool siteTool = new SiteTool()

    @TaskAction
    def delopy() {
        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer(containerConfiguration);

        final wagonManager = container.lookup(WagonManager.ROLE)
        final Repository repository = new Repository("id", url)

        final Wagon wagon =  getWagon( repository, wagonManager );
        final ProxyInfo proxyInfo
        final String relativeDir = getDeployModuleDirectory()


        AuthenticationInfo authenticationInfo = wagonManager.getAuthenticationInfo( repository.getId() );


        Debug debug = new Debug();

        wagon.addSessionListener( debug );

        wagon.addTransferListener( debug );


        if ( proxyInfo != null )
        {
            getLog().debug( "connect with proxyInfo" );
            wagon.connect( repository, authenticationInfo, proxyInfo );
        }
        else if ( proxyInfo == null && authenticationInfo != null )
        {
            getLog().debug( "connect with authenticationInfo and without proxyInfo" );
            wagon.connect( repository, authenticationInfo );
        }
        else
        {
            //getLog().debug( "connect without authenticationInfo and without proxyInfo" );
            println "connect without authenticationInfo and without proxyInfo"
            wagon.connect( repository );
        }

        wagon.putDirectory(inputDirectory, relativeDir)
    }

    private Wagon getWagon( final Repository repository, final WagonManager manager )
    throws MojoExecutionException
    {
        final Wagon wagon;

        try
        {
            wagon = manager.getWagon( repository );
        }
        catch ( UnsupportedProtocolException e )
        {
            e.printStackTrace()
        }
        catch ( TransferFailedException e )
        {
            throw new MojoExecutionException( "Unable to configure Wagon: '" + repository.getProtocol() + "'", e );
        }

        if ( !wagon.supportsDirectoryCopy() )
        {
            throw new MojoExecutionException(
                    "Wagon protocol '" + repository.getProtocol() + "' doesn't support directory copying" );
        }

        return wagon;
    }

    private String getSupportedProtocols()
    {
        try
        {
            Set<String> protocols = container.lookupMap( Wagon.class ).keySet();

            return StringUtils.join( protocols.iterator(), ", " );
        }
        catch ( ComponentLookupException e )
        {
            getLog().error( e );
        }
        return "";
    }

    def String getDeployModuleDirectory() {
        println "project.getRootProject():" + project.getRootProject()

        String relative = siteTool.getRelativePath( project.projectDir.toString(), project.rootDir.toString())

        return ( "".equals( relative ) ) ? "./" : relative;
    }
}

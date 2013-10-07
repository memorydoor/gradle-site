package org.oclc.gradle.site

import org.apache.maven.artifact.manager.WagonManager
import org.apache.maven.wagon.TransferFailedException
import org.apache.maven.wagon.UnsupportedProtocolException
import org.apache.maven.wagon.Wagon
import org.apache.maven.wagon.authentication.AuthenticationInfo
import org.apache.maven.wagon.observers.Debug
import org.apache.maven.wagon.proxy.ProxyInfo
import org.apache.maven.wagon.repository.Repository
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusConstants
import org.codehaus.plexus.PlexusContainer
import org.codehaus.plexus.component.repository.exception.ComponentLookupException
import org.codehaus.plexus.context.Context
import org.codehaus.plexus.context.ContextException
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable
import org.codehaus.plexus.util.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.mvn3.org.apache.maven.plugin.MojoExecutionException

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/7/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteDeployTask extends DefaultTask {

    @InputDirectory
    File inputDir = getProject().file("out/site")

    private PlexusContainer container;

    @TaskAction
    def delopy() {
        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer();

        final wagonManager = container.lookup(WagonManager.ROLE)
        final Repository repository = new Repository("id", "file:C:/temp")

        println "wagonManager" + wagonManager


        final Wagon wagon =  getWagon( repository, wagonManager );
        final ProxyInfo proxyInfo
        final List<Locale> localesList
        final String relativeDir = "."


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

        wagon.putDirectory(inputDir, relativeDir)
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
            /*String shortMessage =
                "Unsupported protocol: '" + repository.getProtocol() + "' for site deployment to "
            + "distributionManagement.site.url=" + repository.getUrl() + ".";
            String longMessage =
                "\n" + shortMessage + "\n" +
                        "Currently supported protocols are: " + getSupportedProtocols() + ".\n"
            + "    Protocols may be added through wagon providers.\n"
            + "    For more information, see "
            + "http://maven.apache.org/plugins/maven-site-plugin/examples/adding-deploy-protocol.html";

            getLog().error( longMessage );*/
            e.printStackTrace()

            //throw new MojoExecutionException( shortMessage );
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
            // in the unexpected case there is a problem when instantiating a wagon provider
            getLog().error( e );
        }
        return "";
    }

    public void contextualize( Context context )
    throws ContextException
    {
        println "contextualize"
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}

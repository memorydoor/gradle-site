package org.oclc.gradle.site

import org.apache.maven.doxia.site.decoration.DecorationModel
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader
import org.apache.maven.doxia.siterenderer.Renderer
import org.apache.maven.doxia.siterenderer.SiteRenderingContext
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusContainer
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTask extends DefaultTask {

    @Override
    def Task doFirst(Action<? super Task> action) {
        def task = super.doFirst(action)
        //project = task.getProject().clone()
        //set project info into SiteModel

        return task
    }

    @TaskAction
    def generateSite() {
        def siteDir = getProject().file("src/site")
        def outputDir = getProject().getBuildDir()

        def reader = new DecorationXpp3Reader();
        reader.read(new FileReader(new File(siteDir, "site.xml")))
        DecorationModel decoration = new DecorationModel();

        //SiteRenderingContext ctxt = getSiteRenderingContext(decoration, siteDir, true);
        SiteRenderingContext ctxt

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer( containerConfiguration );;

        Renderer renderer = container.lookup(Renderer.ROLE)

        final Map<String, String> templateProp = new HashMap<String, String>();
        templateProp.put( "outputEncoding", "UTF-8" );
        ctxt = renderer.createContextForSkin( new File(siteDir, "site.zip"), templateProp, decoration,
                "String defaultWindowTitle", Locale.getDefault())
        ctxt.setUsingDefaultTemplate( true );
        ctxt.addSiteDirectory( siteDir );
        ctxt.setValidate( false );

        renderer.render(renderer.locateDocumentFiles(ctxt).values(), ctxt, outputDir)
    }




    private SiteRenderingContext getSiteRenderingContext( DecorationModel decoration, File siteDir, boolean validate )
    {
        SiteRenderingContext ctxt = new SiteRenderingContext();
        ctxt.setTemplateName( "default-site.vm" );
        URL url = SiteTask.class.getResource("/org/apache/maven/doxia/siterenderer/resources")



        ClassLoader classLoader = new URLClassLoader(url);


        ctxt.setTemplateClassLoader(classLoader)
        ctxt.setUsingDefaultTemplate( true );
        final Map<String, String> templateProp = new HashMap<String, String>();
        templateProp.put( "outputEncoding", "UTF-8" );
        ctxt.setTemplateProperties( templateProp );
        ctxt.setDecoration( decoration );
        ctxt.addSiteDirectory( siteDir );
        ctxt.setValidate( validate );

        return ctxt;
    }

}

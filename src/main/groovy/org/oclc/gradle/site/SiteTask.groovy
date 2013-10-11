package org.oclc.gradle.site

import org.apache.maven.doxia.sink.render.RenderingContext
import org.apache.maven.doxia.site.decoration.Banner
import org.apache.maven.doxia.site.decoration.DecorationModel
import org.apache.maven.doxia.site.decoration.Skin
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader
import org.apache.maven.doxia.siterenderer.Renderer
import org.apache.maven.doxia.siterenderer.SiteRenderingContext
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusContainer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.oclc.gradle.doxia.tools.SiteTool

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTask extends DefaultTask {

    @InputDirectory
    File siteDirectory = getProject().file(SitePluginExtension.DEFAULT_SITE_DIRECTORY)

    @OutputDirectory
    File outputDirectory = project.file(SitePluginExtension.DEFAULT_OUTPUT_DIRECTORY)

    Skin skin

    SiteTool siteTool = new SiteTool()

    @TaskAction
    def generateSite() {

        logger.info("Starting to generate site for Project:" + project.name)
        outputDirectory.mkdirs()

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer(containerConfiguration);

        def renderer = (Renderer) container.lookup(Renderer.ROLE)
        def projectRenderer = (Renderer) container.lookup(Renderer.ROLE, "project")



        def reader = new DecorationXpp3Reader();
        DecorationModel decoration = reader.read(new FileReader(new File(siteDirectory, "site.xml")))

        siteTool.populateModulesMenu(project, decoration)

        def projectName = project.name

        decoration.name = projectName
        Banner bannerLeft = new Banner()
        bannerLeft.name = projectName
        decoration.bannerLeft = bannerLeft


        final Map<String, Object> templateProp = new HashMap<String, Object>();
        templateProp.put( "outputEncoding", "UTF-8" );
        templateProp.put( "project", project)

        SiteRenderingContext siteRenderingContext = renderer.createContextForSkin( siteTool.getSkinJarFile(skin, project), templateProp, decoration,
                projectName, Locale.getDefault())
        siteRenderingContext.setUsingDefaultTemplate( true );
        siteRenderingContext.addSiteDirectory( siteDirectory );
        siteRenderingContext.setValidate( false );


        renderer.render(renderer.locateDocumentFiles(siteRenderingContext).values(), siteRenderingContext, outputDirectory)

        def files = projectRenderer.locateDocumentFiles(siteRenderingContext)
        def filesValues = files.values()
        println "files:" + files
        println "files.values:" + filesValues

        projectRenderer.render(filesValues, siteRenderingContext, outputDirectory)

        //index.html
        //this is temporary, will Need to extract into "project Info" module
        /*
        def writer = new OutputStreamWriter( new FileOutputStream( new File( outputDirectory, "index.html" ) ), "UTF-8" );
        RenderingContext context = new RenderingContext( outputDirectory, "index.html" );
        SiteRendererSink sink = new SiteRendererSink( context );

        sink.body()
        sink.sectionTitle1()
        sink.text("About "  + project.name)
        sink.sectionTitle1_()

        sink.paragraph()
        sink.text(project.description)
        sink.paragraph_()
        sink.body_()

        renderer.generateDocument( writer, sink, siteRenderingContext );
        */
        logger.info("Finished to generate site for Project:" + project.name)
    }

}

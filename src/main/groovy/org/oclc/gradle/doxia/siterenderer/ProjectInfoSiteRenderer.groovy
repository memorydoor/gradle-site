package org.oclc.gradle.doxia.siterenderer

import org.apache.maven.doxia.Doxia
import org.apache.maven.doxia.logging.PlexusLoggerWrapper
import org.apache.maven.doxia.module.site.SiteModule
import org.apache.maven.doxia.module.site.manager.SiteModuleManager
import org.apache.maven.doxia.module.site.manager.SiteModuleNotFoundException
import org.apache.maven.doxia.parser.Parser
import org.apache.maven.doxia.parser.manager.ParserNotFoundException
import org.apache.maven.doxia.sink.render.RenderingContext
import org.apache.maven.doxia.siterenderer.DefaultSiteRenderer
import org.apache.maven.doxia.siterenderer.DocumentRenderer
import org.apache.maven.doxia.siterenderer.DoxiaDocumentRenderer
import org.apache.maven.doxia.siterenderer.ModuleReference
import org.apache.maven.doxia.siterenderer.Renderer
import org.apache.maven.doxia.siterenderer.RendererException
import org.apache.maven.doxia.siterenderer.SiteRenderingContext
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink
import org.apache.velocity.Template
import org.apache.velocity.context.Context
import org.apache.velocity.tools.ToolManager
import org.codehaus.plexus.component.annotations.Component
import org.codehaus.plexus.component.annotations.Requirement
import org.codehaus.plexus.i18n.I18N
import org.codehaus.plexus.util.FileUtils
import org.codehaus.plexus.util.IOUtil
import org.codehaus.plexus.util.Os
import org.codehaus.plexus.util.PathTool
import org.codehaus.plexus.util.ReaderFactory
import org.codehaus.plexus.util.StringUtils
import org.codehaus.plexus.util.WriterFactory
import org.codehaus.plexus.velocity.SiteResourceLoader
import org.codehaus.plexus.velocity.VelocityComponent

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/10/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Component( role = Renderer.class, hint = "project")
class ProjectInfoSiteRenderer extends  DefaultSiteRenderer {

    @Requirement
    private VelocityComponent velocity;

    @Requirement
    private SiteModuleManager siteModuleManager;

    @Requirement
    private Doxia doxia;

    @Requirement
    private I18N i18n;

    @Override
    Map<String, DocumentRenderer> locateDocumentFiles(SiteRenderingContext siteRenderingContext) throws IOException, RendererException {
        Map<String, DocumentRenderer> files = new LinkedHashMap<String, DocumentRenderer>();
        Map<String, String> moduleExcludes = siteRenderingContext.getModuleExcludes();
        Map<String, SiteModule> siteModules = new HashMap<String, SiteModule>();
        Collection<SiteModule> modules = siteModuleManager.getSiteModules();

        for (SiteModule module : modules) {
            siteModules.put(module.getParserId(), module)
        }

        for ( File siteDirectory : siteRenderingContext.getSiteDirectories() )
        {
            URL url = getClass().getResource("/projectinfo/.list")
            InputStream fileListIs = getClass().getResourceAsStream("/projectinfo/.list")




            def filesName =  IOUtil.toString(fileListIs).split(",")


            for (def fileName : filesName) {
                def baseName = fileName.substring(0, fileName.indexOf(".md"))
                SiteModule markdownModule = siteModules.get("markdown")
                File moduleBasedir = new File( siteDirectory, "" );

                RenderingContext context =
                    new RenderingContext( moduleBasedir, fileName, markdownModule.getParserId(), markdownModule.getExtension() );

                if ( fileName.toLowerCase( Locale.ENGLISH ).endsWith( ".vm" ) )
                {
                    context.setAttribute( "velocity", "true" );
                }

                files.put( baseName + ".html", new DoxiaDocumentRenderer( context ))
            }
        }

        return files;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    @Override
    void render(Collection<DocumentRenderer> documents, SiteRenderingContext siteRenderingContext, File outputDirectory) throws RendererException, IOException {

        for ( DocumentRenderer docRenderer : documents )
        {
            RenderingContext renderingContext = docRenderer.getRenderingContext();

            File outputFile = new File( outputDirectory, docRenderer.getOutputName() );


            boolean modified = !outputFile.exists() ||  ( siteRenderingContext.getDecoration().getLastModified() > outputFile.lastModified() );

            if ( modified || docRenderer.isOverwrite() )
            {
                if ( !outputFile.getParentFile().exists() )
                {
                    outputFile.getParentFile().mkdirs();
                }

                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Generating " + outputFile );
                }

                Writer writer = null;
                try
                {
                    writer = WriterFactory.newWriter( outputFile, siteRenderingContext.getOutputEncoding() );
                    docRenderer.renderDocument( writer, this, siteRenderingContext );
                }
                finally
                {
                    IOUtil.close( writer );
                }
            }
            else
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( inputFile + " unchanged, not regenerating..." );
                }
            }
        }

    }

    @Override
    void renderDocument(Writer writer, RenderingContext renderingContext, SiteRenderingContext siteContext) throws RendererException, FileNotFoundException, UnsupportedEncodingException {
        SiteRendererSink sink = new SiteRendererSink( renderingContext );

        println renderingContext.getInputName()
        println renderingContext.getBasedir()

        //String url = renderingContext.getAttribute("url")
        InputStream is = getClass().getResourceAsStream("/projectinfo/" + renderingContext.getInputName())


        //File doc = new File( renderingContext.getBasedir(), renderingContext.getInputName() );

        Reader reader = null;
        try
        {
            //String resource = doc.getAbsolutePath();
            println renderingContext.getParserId()
            Parser parser = doxia.getParser( renderingContext.getParserId() );
            println parser.getType()

            println "velocity:" + velocity
            println "velocity.getEngine():" + velocity.getEngine()
            //println "velocity.getEngine():" + velocity.getEngine().getTemplate("default-site.vm")


            // TODO: DOXIA-111: the filter used here must be checked generally.
            if ( renderingContext.getAttribute( "velocity" ) != null )
            {
                try
                {
                    //SiteResourceLoader.setResource( resource );

                    Context vc = createVelocityContext( sink, siteContext );

                    StringWriter sw = new StringWriter();

                    velocity.getEngine().mergeTemplate( resource, siteContext.getInputEncoding(), vc, sw );

                    reader = new StringReader( sw.toString() );
//                    if ( parser.getType() == Parser.XML_TYPE && siteContext.isValidate() )
//                    {
//                        reader = validate( reader, resource );
//                    }
                }
                catch ( Exception e )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().error( "Error parsing " + resource + " as a velocity template, using as text.", e );
                    }
                    else
                    {
                        getLogger().error( "Error parsing " + resource + " as a velocity template, using as text." );
                    }
                }
            }
            else
            {
                switch ( parser.getType() )
                {
                    case Parser.XML_TYPE:
                        reader = ReaderFactory.newXmlReader(is);
                        if ( siteContext.isValidate() )
                        {
                            //reader = validate( reader, resource );
                        }
                        break;

                    case Parser.TXT_TYPE:
                    case Parser.UNKNOWN_TYPE:
                    default:
                        //reader = ReaderFactory.newReader( doc, siteContext.getInputEncoding() );
                        reader = ReaderFactory.newReader( is, siteContext.getInputEncoding() )
                }
            }
            sink.enableLogging( new PlexusLoggerWrapper( getLogger() ) );

            if ( reader == null ) // can happen if velocity throws above
            {
                throw new RendererException( "Error getting a parser for '" + doc + "'" );
            }
            doxia.parse( reader, renderingContext.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new RendererException( "Error getting a parser for '" + doc + "': " + e.getMessage(), e );
        }
        catch ( ParseException e )
        {
            throw new RendererException( "Error parsing '"
                    + doc + "': line [" + e.getLineNumber() + "] " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new RendererException( "IOException when processing '" + doc + "'", e );
        }
        finally
        {
            sink.flush();

            sink.close();

            IOUtil.close( reader );
        }

        generateDocument( writer, sink, siteContext );

    }

    @Override
    void generateDocument(Writer writer, SiteRendererSink sink, SiteRenderingContext siteRenderingContext) throws RendererException {
        println "this is generateDocument"
        Context context = createVelocityContext( sink, siteRenderingContext );

        writeTemplate( writer, context, siteRenderingContext );
    }

    private Context createVelocityContext( SiteRendererSink sink, SiteRenderingContext siteRenderingContext )
    {
        ToolManager toolManager = new ToolManager( true );
        Context context = toolManager.createContext();

        // ----------------------------------------------------------------------
        // Data objects
        // ----------------------------------------------------------------------

        RenderingContext renderingContext = sink.getRenderingContext();
        context.put( "relativePath", renderingContext.getRelativePath() );

        // Add infos from document
        context.put( "authors", sink.getAuthors() );

        context.put( "shortTitle", sink.getTitle() );

        // DOXIASITETOOLS-70: Prepend the project name to the title, if any
        String title = "";
        if ( siteRenderingContext.getDecoration() != null
                && siteRenderingContext.getDecoration().getName() != null )
        {
            title = siteRenderingContext.getDecoration().getName();
        }
        else if ( siteRenderingContext.getDefaultWindowTitle() != null )
        {
            title = siteRenderingContext.getDefaultWindowTitle();
        }

        if ( title.length() > 0 )
        {
            title += " - ";
        }
        title += sink.getTitle();

        context.put( "title", title );

        context.put( "headContent", sink.getHead() );

        context.put( "bodyContent", sink.getBody() );

        context.put( "decoration", siteRenderingContext.getDecoration() );

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" );
        if ( StringUtils.isNotEmpty( sink.getDate() ) )
        {
            try
            {
                // we support only ISO-8601 date
                context.put( "dateCreation",
                        sdf.format( new SimpleDateFormat( "yyyy-MM-dd" ).parse( sink.getDate() ) ) );
            }
            catch ( java.text.ParseException e )
            {
                getLogger().debug( "Could not parse date: " + sink.getDate() + ", ignoring!", e );
            }
        }
        context.put( "dateRevision", sdf.format( new Date() ) );

        context.put( "currentDate", new Date() );

        context.put( "publishDate", siteRenderingContext.getPublishDate() );

        Locale locale = siteRenderingContext.getLocale();

        DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.DEFAULT, locale );

        if ( siteRenderingContext.getDecoration().getPublishDate() != null )
        {
            if ( StringUtils.isNotBlank( siteRenderingContext.getDecoration().getPublishDate().getFormat() ) )
            {
                dateFormat =
                    new SimpleDateFormat( siteRenderingContext.getDecoration().getPublishDate().getFormat(), locale );
            }
        }

        context.put( "dateFormat", dateFormat );

        String currentFileName = renderingContext.getOutputName().replace( '\\', '/' );
        context.put( "currentFileName", currentFileName );

        context.put( "alignedFileName", PathTool.calculateLink( currentFileName, renderingContext.getRelativePath() ) );

        context.put( "locale", locale );
        context.put( "supportedLocales", Collections.unmodifiableList( siteRenderingContext.getSiteLocales() ) );

        InputStream inputStream = null;
        try
        {
            inputStream = this.getClass().getClassLoader().getResourceAsStream( "META-INF/maven/org.apache.maven.doxia/doxia-site-renderer/pom.properties" );
            if ( inputStream == null )
            {
                getLogger().debug( "pom.properties for doxia-site-renderer could not be found." );
            }
            else
            {
                Properties properties = new Properties();
                properties.load( inputStream );
                context.put( "doxiaSiteRendererVersion", properties.getProperty( "version" ) );
            }
        }
        catch( IOException e )
        {
            getLogger().debug( "Failed to load pom.properties, so doxiaVersion is not available in the velocityContext." );
        }
        finally
        {
            IOUtil.close( inputStream );
        }

        // Add user properties
        Map<String, ?> templateProperties = siteRenderingContext.getTemplateProperties();

        if ( templateProperties != null )
        {
            for ( Map.Entry<String, ?> entry : templateProperties.entrySet() )
            {
                context.put( entry.getKey(), entry.getValue() );
            }
        }

        // ----------------------------------------------------------------------
        // Tools
        // ----------------------------------------------------------------------

        context.put( "PathTool", new PathTool() );

        context.put( "FileUtils", new FileUtils() );

        context.put( "StringUtils", new StringUtils() );

        context.put( "i18n", i18n );

        return context;
    }


    private void writeTemplate( Writer writer, Context context, SiteRenderingContext siteContext )
    throws RendererException
    {
        ClassLoader old = null;

        if ( siteContext.getTemplateClassLoader() != null )
        {
            // -------------------------------------------------------------------------
            // If no template classloader was set we'll just use the context classloader
            // -------------------------------------------------------------------------

            old = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader( siteContext.getTemplateClassLoader() );
        }

        try
        {
            processTemplate( siteContext.getTemplateName(), context, writer );
        }
        finally
        {
            IOUtil.close( writer );

            if ( old != null )
            {
                Thread.currentThread().setContextClassLoader( old );
            }
        }
    }

    /**
     * @noinspection OverlyBroadCatchBlock,UnusedCatchParameter
     */
    private void processTemplate( String templateName, Context context, Writer writer )
    throws RendererException
    {
        Template template;

        try
        {
            template = velocity.getEngine().getTemplate( templateName );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Could not find the template '" + templateName, e );
        }

        try
        {
            template.merge( context, writer );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Error while generating code.", e );
        }
    }


    private void addModuleFiles( File moduleBasedir, SiteModule module, String excludes,
                                 Map<String, DocumentRenderer> files )
    throws IOException, RendererException
    {
        if ( moduleBasedir.exists() )
        {
            @SuppressWarnings ( "unchecked" )
            List<String> allFiles = FileUtils.getFileNames( moduleBasedir, "**/*.*", excludes, false );

            String lowerCaseExtension = module.getExtension().toLowerCase( Locale.ENGLISH );
            List<String> docs = new LinkedList<String>( allFiles );
            // Take care of extension case
            for ( Iterator<String> it = docs.iterator(); it.hasNext(); )
            {
                String name = it.next().trim();

                if ( !name.toLowerCase( Locale.ENGLISH ).endsWith( "." + lowerCaseExtension ) )
                {
                    it.remove();
                }
            }

            List<String> velocityFiles = new LinkedList<String>( allFiles );
            // *.xml.vm
            for ( Iterator<String> it = velocityFiles.iterator(); it.hasNext(); )
            {
                String name = it.next().trim();

                if ( !name.toLowerCase( Locale.ENGLISH ).endsWith( lowerCaseExtension + ".vm" ) )
                {
                    it.remove();
                }
            }
            docs.addAll( velocityFiles );

            for ( String doc : docs )
            {
                String docc = doc.trim();

                RenderingContext context =
                    new RenderingContext( moduleBasedir, docc, module.getParserId(), module.getExtension() );

                // TODO: DOXIA-111: we need a general filter here that knows how to alter the context
                if ( docc.toLowerCase( Locale.ENGLISH ).endsWith( ".vm" ) )
                {
                    context.setAttribute( "velocity", "true" );
                }

                String key = context.getOutputName();
                key = StringUtils.replace( key, "\\", "/" );

                if ( files.containsKey( key ) )
                {
                    DocumentRenderer renderer = files.get( key );

                    RenderingContext originalContext = renderer.getRenderingContext();

                    File originalDoc = new File( originalContext.getBasedir(), originalContext.getInputName() );

                    throw new RendererException( "Files '" + module.getSourceDirectory() + File.separator + docc
                            + "' clashes with existing '" + originalDoc + "'." );
                }
                // -----------------------------------------------------------------------
                // Handle key without case differences
                // -----------------------------------------------------------------------
                for ( Map.Entry<String, DocumentRenderer> entry : files.entrySet() )
                {
                    if ( entry.getKey().equalsIgnoreCase( key ) )
                    {
                        RenderingContext originalContext = entry.getValue().getRenderingContext();

                        File originalDoc = new File( originalContext.getBasedir(), originalContext.getInputName() );

                        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
                        {
                            throw new RendererException( "Files '" + module.getSourceDirectory() + File.separator
                                    + docc + "' clashes with existing '" + originalDoc + "'." );
                        }

                        if ( getLogger().isWarnEnabled() )
                        {
                            getLogger().warn(
                                    "Files '" + module.getSourceDirectory() + File.separator + docc
                                            + "' could clashes with existing '" + originalDoc + "'." );
                        }
                    }
                }

                files.put( key, new DoxiaDocumentRenderer( context ) );
            }
        }
    }
}

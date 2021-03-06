package org.oclc.gradle.doxia.tools

import org.apache.commons.io.FilenameUtils
import org.apache.ivy.core.IvyContext
import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.descriptor.DefaultArtifact
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.maven.doxia.site.decoration.DecorationModel
import org.apache.maven.doxia.site.decoration.Menu
import org.apache.maven.doxia.site.decoration.MenuItem
import org.apache.maven.doxia.site.decoration.Skin
import org.codehaus.plexus.i18n.I18N
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.DefaultArtifactIdentifier
import org.gradle.api.internal.artifacts.ivyservice.DefaultBuildableArtifactResolveResult

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTool {

    I18N i18n

    def getSkinJarFileFromRepository(Skin skin, MavenArtifactRepository repositoryHandler) {
        if (skin == null) {
            skin = Skin.getDefaultSkin()
        }

        ModuleRevisionId revisionId = ModuleRevisionId.newInstance(skin.getGroupId(), skin.getArtifactId(), skin.getVersion());

        Artifact artifact = new DefaultArtifact(revisionId, new Date(), revisionId.getName(), "jar", "jar", false);

        DefaultArtifactIdentifier artifactIdentifier = new DefaultArtifactIdentifier(artifact)

        DefaultBuildableArtifactResolveResult resolveResult = new DefaultBuildableArtifactResolveResult()

        def moduleVersionRepository = repositoryHandler.createRealResolver();

        moduleVersionRepository.setSettings(IvyContext.getContext().getSettings())


        moduleVersionRepository.resolve(artifactIdentifier, resolveResult, null)

        return resolveResult.getFile()

    }

    def populateModulesMenu(Project project, DecorationModel decorationModel) {
        Menu menu = decorationModel.getMenuRef( "modules" );

        if (menu == null) {
            return;
        }

        menu.name = "Modules"

        for (Project subProject : project.subprojects) {
            MenuItem item = new MenuItem()
            item.name = subProject.name
            item.href = "./" + subProject.name + "/index.html"

            menu.addItem(item)
        }

    }

    /** {@inheritDoc} */
    public void populateReportsMenu( DecorationModel decorationModel)
    {
        Menu menu = decorationModel.getMenuRef( "reports" );


        if ( menu == null )
        {
            return;
        }

        menu.setName( "projectdocumentation" );

        //javadoc
        MenuItem item = createCategoryMenu( "Project Reports",
                "/project-reports.html");
        menu.addItem( item );
    }

    private MenuItem createCategoryMenu( String name, String href)
    {
        MenuItem item = new MenuItem();
        item.setName( name );
        item.setCollapse( true );
        item.setHref( href );

        return item;
    }


    def getRelativePath(String to, String from) {
        if ( to == null )
        {
            throw new IllegalArgumentException( "The parameter 'to' can not be null" );
        }
        if ( from == null )
        {
            throw new IllegalArgumentException( "The parameter 'from' can not be null" );
        }

        URL toUrl = null;
        URL fromUrl = null;

        String toPath = to;
        String fromPath = from;

        try
        {
            toUrl = new URL( to );
        }
        catch ( MalformedURLException e )
        {
            try
            {
                toUrl = new File( getNormalizedPath( to ) ).toURI().toURL();
            }
            catch ( MalformedURLException e1 )
            {
                e1.printStackTrace()
            }
        }

        try
        {
            fromUrl = new URL( from );
        }
        catch ( MalformedURLException e )
        {
            try
            {
                fromUrl = new File( getNormalizedPath( from ) ).toURI().toURL();
            }
            catch ( MalformedURLException e1 )
            {
                println "Unable to load a URL for '" + from + "': " + e.getMessage()
            }
        }

        if ( toUrl != null && fromUrl != null )
        {
            // URLs, determine if they share protocol and domain info

            if ( ( toUrl.getProtocol().equalsIgnoreCase( fromUrl.getProtocol() ) )
                    && ( toUrl.getHost().equalsIgnoreCase( fromUrl.getHost() ) )
                    && ( toUrl.getPort() == fromUrl.getPort() ) )
            {
                // shared URL domain details, use URI to determine relative path

                toPath = toUrl.getFile();
                fromPath = fromUrl.getFile();
            }
            else
            {
                // don't share basic URL information, no relative available

                return to;
            }
        }
        else if ( ( toUrl != null && fromUrl == null ) || ( toUrl == null && fromUrl != null ) )
        {
            // one is a URL and the other isn't, no relative available.

            return to;
        }

        // either the two locations are not URLs or if they are they
        // share the common protocol and domain info and we are left
        // with their URI information

        String relativePath = getRelativeFilePath( fromPath, toPath );

        if ( relativePath == null )
        {
            relativePath = to;
        }

        return relativePath;
    }

    private static String getRelativeFilePath( final String oldPath, final String newPath )
    {
        // normalize the path delimiters

        String fromPath = new File( oldPath ).getPath();
        String toPath = new File( newPath ).getPath();

        // strip any leading slashes if its a windows path
        if ( toPath.matches( "^\\[a-zA-Z]:" ) )
        {
            toPath = toPath.substring( 1 );
        }
        if ( fromPath.matches( "^\\[a-zA-Z]:" ) )
        {
            fromPath = fromPath.substring( 1 );
        }

        // lowercase windows drive letters.
        if ( fromPath.startsWith( ":", 1 ) )
        {
            fromPath = Character.toLowerCase( fromPath.charAt( 0 ) ).toString() + fromPath.substring( 1 );
        }
        if ( toPath.startsWith( ":", 1 ) )
        {
            toPath = Character.toLowerCase( toPath.charAt( 0 ) ).toString() + toPath.substring( 1 );
        }

        // check for the presence of windows drives. No relative way of
        // traversing from one to the other.

        if ( ( toPath.startsWith( ":", 1 ) && fromPath.startsWith( ":", 1 ) )
                && ( !toPath.substring( 0, 1 ).equals( fromPath.substring( 0, 1 ) ) ) )
        {
            // they both have drive path element but they don't match, no
            // relative path

            return null;
        }

        if ( ( toPath.startsWith( ":", 1 ) && !fromPath.startsWith( ":", 1 ) )
                || ( !toPath.startsWith( ":", 1 ) && fromPath.startsWith( ":", 1 ) ) )
        {

            // one has a drive path element and the other doesn't, no relative
            // path.

            return null;

        }

        final String relativePath = buildRelativePath( toPath, fromPath, File.separatorChar );

        return relativePath.toString();
    }

    private static String buildRelativePath( final String toPath,  final String fromPath, final char separatorChar )
    {
        // use tokenizer to traverse paths and for lazy checking
        StringTokenizer toTokeniser = new StringTokenizer( toPath, String.valueOf( separatorChar ) );
        StringTokenizer fromTokeniser = new StringTokenizer( fromPath, String.valueOf( separatorChar ) );

        int count = 0;

        // walk along the to path looking for divergence from the from path
        while ( toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens() )
        {
            if ( separatorChar == '\\' )
            {
                if ( !fromTokeniser.nextToken().equalsIgnoreCase( toTokeniser.nextToken() ) )
                {
                    break;
                }
            }
            else
            {
                if ( !fromTokeniser.nextToken().equals( toTokeniser.nextToken() ) )
                {
                    break;
                }
            }

            count++;
        }

        // reinitialize the tokenizers to count positions to retrieve the
        // gobbled token

        toTokeniser = new StringTokenizer( toPath, String.valueOf( separatorChar ) );
        fromTokeniser = new StringTokenizer( fromPath, String.valueOf( separatorChar ) );

        while ( count-- > 0 )
        {
            fromTokeniser.nextToken();
            toTokeniser.nextToken();
        }

        StringBuilder relativePath = new StringBuilder();

        // add back refs for the rest of from location.
        while ( fromTokeniser.hasMoreTokens() )
        {
            fromTokeniser.nextToken();

            relativePath.append( ".." );

            if ( fromTokeniser.hasMoreTokens() )
            {
                relativePath.append( separatorChar );
            }
        }

        if ( relativePath.length() != 0 && toTokeniser.hasMoreTokens() )
        {
            relativePath.append( separatorChar );
        }

        // add fwd fills for whatever's left of to.
        while ( toTokeniser.hasMoreTokens() )
        {
            relativePath.append( toTokeniser.nextToken() );

            if ( toTokeniser.hasMoreTokens() )
            {
                relativePath.append( separatorChar );
            }
        }
        return relativePath.toString();
    }

    protected static String getNormalizedPath( String path )
    {
        String normalized = FilenameUtils.normalize( path );
        if ( normalized == null )
        {
            normalized = path;
        }
        return ( normalized == null ) ? null : normalized.replace( '\\', '/' );
    }

}

package org.oclc.gradle.report.projectinfo

import org.apache.maven.doxia.siterenderer.Renderer
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusContainer

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectIndexPageReport {

    public void executeReport()
    {
        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer(containerConfiguration);

        Renderer renderer = container.lookup(Renderer.ROLE)


    }
}

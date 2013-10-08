package org.oclc.gradle.doxia.tools

import junit.framework.TestCase
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
class SiteToolTest extends  TestCase {

    public void testGetRelativePath()
    throws Exception
    {
        SiteTool tool = new SiteTool()
        assertNotNull( tool );

        String to = "http://maven.apache.org";
        String from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "" );

        to = "http://maven.apache.org";
        from = "http://maven.apache.org/";
        assertEquals( tool.getRelativePath( to, from ), "" );

        to = "http://maven.apache.org/";
        from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "" );

        to = "http://maven.apache.org/";
        from = "http://maven.apache.org/";
        assertEquals( tool.getRelativePath( to, from ), "" );

        to = "http://maven.apache.org/";
        from = "http://maven.apache.org/plugins/maven-site-plugin";
        assertEquals( tool.getRelativePath( to, from ), ".." + File.separator + ".." );
        to = "http://maven.apache.org";
        from = "http://maven.apache.org/plugins/maven-site-plugin/";
        assertEquals( tool.getRelativePath( to, from ), ".." + File.separator + ".." );
        to = "http://maven.apache.org/";
        from = "http://maven.apache.org/plugins/maven-site-plugin/";
        assertEquals( tool.getRelativePath( to, from ), ".." + File.separator + ".." );
        to = "http://maven.apache.org";
        from = "http://maven.apache.org/plugins/maven-site-plugin";
        assertEquals( tool.getRelativePath( to, from ), ".." + File.separator + ".." );

        to = "http://maven.apache.org/plugins/maven-site-plugin/";
        from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "plugins" + File.separator + "maven-site-plugin" );
        to = "http://maven.apache.org/plugins/maven-site-plugin/";
        from = "http://maven.apache.org/";
        assertEquals( tool.getRelativePath( to, from ), "plugins" + File.separator + "maven-site-plugin" );
        to = "http://maven.apache.org/plugins/maven-site-plugin";
        from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "plugins" + File.separator + "maven-site-plugin" );
        to = "http://maven.apache.org/plugins/maven-site-plugin";
        from = "http://maven.apache.org/";
        assertEquals( tool.getRelativePath( to, from ), "plugins" + File.separator + "maven-site-plugin" );
    }

}
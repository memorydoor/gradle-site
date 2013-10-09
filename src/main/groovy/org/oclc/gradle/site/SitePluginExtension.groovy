package org.oclc.gradle.site

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/9/13
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
class SitePluginExtension {
    public static final String NAME = "site";
    public static final String DEFAULT_OUTPUT_DIRECTORY = "build/site"
    public static final String DEFAULT_SITE_DIRECTORY = "src/site"


    String url
    String outputDirectory
}

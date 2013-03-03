package org.aferre.maven.redmine.plugin.core;

import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * 
 */
public abstract class AbstractRedmineMojo extends AbstractMojo {
	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject mavenProject;
	/**
	 * @parameter expression="${redmine.hostUrl}"
	 */
	protected URL hostUrl;
	/**
	 * @parameter expression="${redmine.apiKey}"
	 */
	protected String apiKey;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isInfoEnabled()) {
			getLog().info("Using host url " + hostUrl);
		}
	}
}

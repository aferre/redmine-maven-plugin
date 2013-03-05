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
	 * @required
	 */
	protected URL hostUrl;
	/**
	 * @parameter expression="${redmine.apiKey}"
	 */
	protected String apiKey;

	/**
	 * @parameter expression="${dryRun}" default-value="false"
	 * @required
	 */
	protected Boolean dryRun;

	/**
	 * @parameter expression="${redmine.abortOnError}" default-value="true"
	 * @required
	 */
	protected Boolean abortOnError;

	/**
	 * @parameter expression="${redmine.projectId}"
	 */
	protected String projectId;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isInfoEnabled()) {
			getLog().info("Redmine Host url: " + hostUrl);
			getLog().info("Redmine porject identifier: " + projectId);
			getLog().info("DryRun: " + dryRun);
			if (apiKey != null)
				getLog().info("Using apikey for authentication.");
			getLog().info("AbortOnError: " + abortOnError);
		}
	}
}

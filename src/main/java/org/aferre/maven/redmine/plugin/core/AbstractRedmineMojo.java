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
	/**
	 * @parameter expression="${redmine.interactive}" default-value="false"
	 * @required
	 */
	protected Boolean interactive;

	/**
	 * @param mavenProject
	 * @param hostUrl
	 * @param apiKey
	 * @param dryRun
	 * @param abortOnError
	 * @param projectId
	 * @param interactive
	 */
	protected AbstractRedmineMojo(MavenProject mavenProject, URL hostUrl,
			String apiKey, Boolean dryRun, Boolean abortOnError,
			String projectId, Boolean interactive) {
		super();
		this.mavenProject = mavenProject;
		this.hostUrl = hostUrl;
		this.apiKey = apiKey;
		this.dryRun = dryRun;
		this.abortOnError = abortOnError;
		this.projectId = projectId;
		this.interactive = interactive;
	}

	protected AbstractRedmineMojo(AbstractRedmineMojo mojo) {
		super();
		this.setLog(mojo.getLog());
		this.mavenProject = mojo.mavenProject;
		this.hostUrl = mojo.hostUrl;
		this.apiKey = mojo.apiKey;
		this.dryRun = mojo.dryRun;
		this.abortOnError = mojo.abortOnError;
		this.projectId = mojo.projectId;
		this.interactive = mojo.interactive;
	}

	public AbstractRedmineMojo() {
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isInfoEnabled()) {
			getLog().info("Redmine Host url: " + hostUrl);
			getLog().info("Redmine project identifier: " + projectId);
			getLog().info("DryRun: " + dryRun);
			getLog().info("Interactive: " + interactive);

			if (apiKey != null)
				getLog().info("Using apikey for authentication.");
			getLog().info("AbortOnError: " + abortOnError);
		}
	}
}

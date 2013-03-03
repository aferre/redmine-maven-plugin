package org.aferre.maven.redmine.plugin.projects;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractRedmineProjectMojo extends AbstractRedmineMojo {

	/**
	 * @parameter expression="${list-issues.projectId}"
	 */
	protected String projectId;

	public AbstractRedmineProjectMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (getLog().isInfoEnabled()) {
			getLog().info("Using projectId " + projectId);
		}

	}
}
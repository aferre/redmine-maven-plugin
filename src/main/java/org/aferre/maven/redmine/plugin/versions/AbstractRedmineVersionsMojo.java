package org.aferre.maven.redmine.plugin.versions;

import java.net.URL;
import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

public abstract class AbstractRedmineVersionsMojo extends AbstractRedmineMojo {

	/**
	 * @parameter expression="${redmine.projectIds}"
	 */
	protected String[] projectIds;

	/**
	 * @parameter expression="${redmine.allProjects}"
	 */
	protected Boolean all;

	/**
	 * @parameter expression="${redmine.versionPrefixes}"
	 */
	protected String[] versionPrefixes;

	/**
	 * @parameter expression="${redmine.versionSuffixes}"
	 */
	protected String[] versionSuffixes;

	/**
	 * @param mavenProject
	 * @param hostUrl
	 * @param apiKey
	 * @param dryRun
	 * @param abortOnError
	 * @param projectId
	 * @param interactive
	 * @param projectIds
	 * @param all
	 */
	protected AbstractRedmineVersionsMojo(MavenProject mavenProject,
			URL hostUrl, String apiKey, Boolean dryRun, Boolean abortOnError,
			String projectId, Boolean interactive, String[] projectIds,
			Boolean all) {
		super(mavenProject, hostUrl, apiKey, dryRun, abortOnError, projectId,
				interactive);
		this.projectIds = projectIds;
		this.all = all;
	}

	protected AbstractRedmineVersionsMojo(AbstractRedmineVersionsMojo mojo) {
		super(mojo);
		this.projectIds = mojo.projectIds;
		this.all = mojo.all;
	}

	public AbstractRedmineVersionsMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (getLog().isInfoEnabled()) {
			if (projectIds != null)
				getLog().info("Using projectIds " + projectIds);
			else if (all)
				getLog().info("Using all projects.");
			else if (projectId != null)
				getLog().info("Using projectId " + projectId);
		}
	}

	protected List<Version> getVersions(RedmineManager mgr, Project project)
			throws RedmineException {
		List<Version> versions = mgr.getVersions(project.getId());
		if (versions.isEmpty()) {
			if (getLog().isInfoEnabled())
				getLog().info("No version");
		} else if (getLog().isInfoEnabled()) {
			getLog().info("**************");
			getLog().info("Project " + project.getName());
			getLog().info("Versions are:");
			for (Version version : versions) {
				getLog().info(Utils.toString(version));
			}
		}
		return versions;
	}
}
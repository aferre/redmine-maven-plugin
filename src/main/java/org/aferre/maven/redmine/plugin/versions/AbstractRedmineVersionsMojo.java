package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

public abstract class AbstractRedmineVersionsMojo extends AbstractRedmineMojo {

	/**
	 * @parameter expression="${redmine..projectIds}"
	 */
	protected String[] projectIds;
	/**
	 * @parameter expression="${redmine..allProjects}"
	 */
	protected Boolean all;

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
		if (getLog().isInfoEnabled()) {
			getLog().info("**************");
			getLog().info("Project " + project.getName());
			getLog().info("Versions are \n");
			for (Version version : versions) {
				getLog().info(Utils.toString(version));
			}
		}
		return versions;
	}
}
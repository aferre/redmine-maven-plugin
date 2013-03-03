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
	 * @parameter expression="${list-versions.projectId}"
	 */
	protected String projectId;
	/**
	 * @parameter expression="${list-versions.projectIds}"
	 */
	protected String[] projectIds;
	/**
	 * @parameter expression="${list-versions.all}"
	 */
	protected Boolean all;

	public AbstractRedmineVersionsMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (getLog().isInfoEnabled()) {
			getLog().info("Using projectId " + projectId);
		}
	}

	protected List<Version> getVersions(RedmineManager mgr, Project project)
			throws RedmineException {
		List<Version> versions = mgr.getVersions(project.getId());
		System.out.println("**************");
		System.out.println("Project " + project.getName());
		System.out.println("Versions are \n");
		for (Version version : versions) {
			Utils.printVersion(version);
		}
		return versions;
	}
}
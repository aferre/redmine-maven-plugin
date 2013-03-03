package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;

/**
 * 
 * @goal list-versions
 * 
 * @phase install
 */
@Mojo(name = "list-versions")
public class ListVersionsMojo extends AbstractRedmineVersionsMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		
		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		if (all != null && all) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving all versions.");
			}
			try {
				List<Project> projects = mgr.getProjects();
				for (Project project : projects) {
					getVersions(mgr, project);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (projectIds != null && projectIds.length != 0) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for projects " + projectIds);
			}
			for (String projectName : projectIds) {
				try {
					Project project = mgr.getProjectByKey(projectName);
					getVersions(mgr, project);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if (projectId != null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for project " + projectId);
			}
			try {
				Project project = mgr.getProjectByKey(projectId);
				getVersions(mgr, project);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (getLog().isErrorEnabled()) {
				getLog().error("No options selected");
			}
		}

	}


}

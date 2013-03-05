package org.aferre.maven.redmine.plugin.projects;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal verify-project
 * 
 * @phase verify
 */
@Mojo(name = "verify-project")
public class VerifyProjectMojo extends AbstractRedmineMojo {

	private Project currentProject;

	private Boolean createIfNotExisting;

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (projectId == null) {
			if (getLog().isErrorEnabled()) {
				getLog().error(
						"You have to provide a projectId. Please dfine the property redmine.projectId");
			}
			if (abortOnError) {

			} else
				return;
		}
		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			List<Project> projects = mgr.getProjects();
			for (Project issue : projects) {
				if (issue.getIdentifier().equals(this.projectId)) {
					currentProject = issue;
					break;
				}
			}
			if (currentProject != null) {
				if (getLog().isDebugEnabled()) {
					getLog().debug(
							"Found corresponding project: "
									+ Utils.toString(currentProject));
				}
			} else {

			}
		} catch (Exception e) {
			// if (getLog().isErrorEnabled())
			System.out.println(e.getMessage());
		}
	}
}

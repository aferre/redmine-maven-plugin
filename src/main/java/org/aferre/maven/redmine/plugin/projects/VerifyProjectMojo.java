package org.aferre.maven.redmine.plugin.projects;

import java.io.Console;
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

	/**
	 * @parameter default-value="${project}"
	 *            expression="${redmine.createProjectIfNotExisting}"
	 */
	private Boolean createProjectIfNotExisting;

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		if (projectId == null) {
			if (getLog().isErrorEnabled()) {
				getLog().error(
						"You have to provide a projectId. Please define the property redmine.projectId");
			}
			if (interactive) {
				Console console = System.console();
				String input = console.readLine("Enter projectId:");
				if (input.isEmpty()) {

				} else {
					projectId = input;
				}
			}
			if (abortOnError) {

			} else
				return;
		}
		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			List<Project> projects = mgr.getProjects();
			for (Project prj : projects) {
				if (prj.getIdentifier().equals(this.projectId)) {
					currentProject = prj;
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
				if (getLog().isDebugEnabled()) {
					getLog().debug("Project has not been found.");
				}
				if (createProjectIfNotExisting) {
					if (interactive) {
						Console console = System.console();
						String input = console
								.readLine("Do you want to create the project (y/n)?");
						if (input.isEmpty() || input.equals("y")) {
							if (getLog().isDebugEnabled()) {
								getLog().debug("Creating project:" + projectId);
							}
							CreateProjectMojo mj = new CreateProjectMojo(
									(AbstractRedmineMojo) this);

							mj.createProject(mgr, projectId);
						} else if (input.equals("n")) {
							return;
						} else {
							return;
						}
					} else {
						if (getLog().isDebugEnabled()) {
							getLog().debug("Creating project:" + projectId);
						}
						CreateProjectMojo mj = new CreateProjectMojo(
								(AbstractRedmineMojo) this);

						mj.createProject(mgr, projectId);
					}

				}
			}
		} catch (Exception e) {
			// if (getLog().isErrorEnabled())
			System.out.println(e.getMessage());
		}
	}
}

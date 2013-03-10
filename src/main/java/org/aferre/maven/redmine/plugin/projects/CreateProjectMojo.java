package org.aferre.maven.redmine.plugin.projects;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;

/**
 * 
 * @goal create-project
 * 
 */
@Mojo(name = "create-project")
public class CreateProjectMojo extends AbstractRedmineMojo {

	public CreateProjectMojo(AbstractRedmineMojo abstractRedmineMojo) {
		super(abstractRedmineMojo);
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		extracted();
	}

	private void extracted() {
		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);
		if (projectId == null) {
			if (getLog().isErrorEnabled()) {
				getLog().error(
						"You have to provide a projectId. Please define the property redmine.projectId");
			}
			if (abortOnError) {

			} else
				return;
		}
		try {
			createProject(mgr, projectId);
		} catch (RedmineAuthenticationException e) {
			e.printStackTrace();
		} catch (RedmineException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 
	 * Tries to create a redmine project based on the maven project pom.
	 * 
	 * @param mgr
	 * @param prjId
	 * @throws RedmineException
	 */
	public void createProject(RedmineManager mgr, String prjId)
			throws RedmineException {
		Project projectByKey = null;
		String projectId2 = prjId;
		try {
			projectByKey = mgr.getProjectByKey(projectId2);
		} catch (NotFoundException e) {
			if (getLog().isDebugEnabled()) {
				getLog().debug("Project not found.");
			}
		}

		if (projectByKey != null) {
			if (getLog().isDebugEnabled()) {
				getLog().debug("Project already created:");
				getLog().debug(Utils.toString(projectByKey));
			}
		} else {
			Project project = new Project();
			project.setIdentifier(projectId2);
			project.setName(mavenProject.getName());
			project.setDescription(mavenProject.getDescription());
			project.setHomepage(mavenProject.getUrl());
			if (getLog().isDebugEnabled()) {
				getLog().debug("Creating project with configuration:");
				getLog().debug(Utils.toString(project));
			}
			Project ret = mgr.createProject(project);
			if (getLog().isDebugEnabled()) {
				getLog().debug("Project successfuly created");
				getLog().debug(Utils.toString(ret));
			}
		}
	}
}

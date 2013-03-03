package org.aferre.maven.redmine.plugin.projects;

import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

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
public class CreateProjectMojo extends AbstractRedmineProjectMojo {
	
	public void execute() throws MojoExecutionException {

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			Project projectByKey = null;

			try {
				projectByKey = mgr.getProjectByKey(projectId);
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
				if (getLog().isDebugEnabled()) {
					getLog().debug("Creating project with configuration:");
				}
				Project project = new Project();
				project.setIdentifier(projectId);
				project.setName(mavenProject.getName());
				project.setDescription(mavenProject.getDescription());
				project.setHomepage(mavenProject.getUrl());
				if (getLog().isDebugEnabled()) {
					getLog().debug(Utils.toString(project));
				}
				Project ret = mgr.createProject(project);
				if (getLog().isDebugEnabled()) {
					getLog().debug("Project successfuly created");
					getLog().debug(Utils.toString(ret));
				}
			}
		} catch (RedmineAuthenticationException e) {
			e.printStackTrace();
		} catch (RedmineException e1) {
			e1.printStackTrace();
		}
	}
}

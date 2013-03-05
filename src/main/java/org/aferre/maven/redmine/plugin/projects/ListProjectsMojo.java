package org.aferre.maven.redmine.plugin.projects;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal list-projects
 * 
 * @phase install
 */
@Mojo(name = "list-projects")
public class ListProjectsMojo extends AbstractRedmineMojo {

	public void execute() throws MojoExecutionException {

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			List<Project> projects = mgr.getProjects();
			for (Project issue : projects) {
				getLog().info(Utils.toString(issue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package org.aferre.maven.redmine.plugin;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal list-issues
 * 
 * @phase install
 */
@Mojo(name = "list-issues")
public class ListIssuesMojo extends AbstractRedmineMojo {

	/**
	 * @parameter expression="${list-issues.projectId}"
	 */
	protected String projectId;

	private static Integer queryId = null; // any

	private static void tryGetIssues(RedmineManager mgr, String projectId)
			throws Exception {
		List<Issue> issues = mgr.getIssues(projectId, queryId);
		for (Issue issue : issues) {
			System.out.println(issue.toString());
		}
	}

	public void execute() throws MojoExecutionException {
		if (getLog().isInfoEnabled()) {
			getLog().info("Using host url " + hostUrl);
			getLog().info("Using projectId " + projectId);
		}

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			tryGetIssues(mgr, projectId);
			List<Project> projects = mgr.getProjects();
			for (Project issue : projects) {
				printProject(issue);
				List<Version> versions = mgr.getVersions(issue.getId());
				for (Version version : versions) {
					printVersion(version);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printVersion(Version version) {
		System.out.println("****\nVersion name: " + version.getName());
		System.out.println("CreatedOn: " + version.getCreatedOn());
		System.out.println("Description: " + version.getDescription());
		System.out.println("DueDate: " + version.getDueDate());
		System.out.println("Id: " + version.getId());
		System.out.println("Project: " + version.getProject());
		System.out.println("Status: " + version.getStatus());
		System.out.println("UpdatedOn: " + version.getUpdatedOn());
	}

	private void printProject(Project issue) {
		System.out.println("****\nProject : " + issue.getName());
		System.out.println("CreatedOn : " + issue.getCreatedOn());
		System.out.println("Description : " + issue.getDescription());
		System.out.println("Homepage : " + issue.getHomepage());
		System.out.println("Id : " + issue.getId());
		System.out.println("Identifier : " + issue.getIdentifier());
		System.out.println("ParentId : " + issue.getParentId());
		System.out.println("Trackers : " + issue.getTrackers());
		System.out.println("UpdatedOn : " + issue.getUpdatedOn());
	}
}

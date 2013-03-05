package org.aferre.maven.redmine.plugin.issues;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

/**
 * 
 * @goal list-issues
 * 
 * @phase install
 */
@Mojo(name = "list-issues")
public class ListIssuesMojo extends AbstractRedmineMojo {

	/**
	 * @parameter expression="${list-issues.allVersions}"
	 */
	protected Boolean allVersions;
	/**
	 * @parameter expression="${list-issues.version}"
	 */
	protected String version;

	private static Integer queryId = null; // any

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			List<Issue> issues = mgr.getIssues(projectId, queryId);
			if (issues.size() == 0) {
				getLog().info("No issues for this project.");
			}
			for (Issue issue : issues) {
				System.out.println(issue.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

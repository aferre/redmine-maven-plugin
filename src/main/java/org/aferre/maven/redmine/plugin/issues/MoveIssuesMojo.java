package org.aferre.maven.redmine.plugin.issues;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

/**
 * 
 * @goal move-issues
 * 
 * @phase install
 */
@Mojo(name = "move-issues")
public class MoveIssuesMojo extends AbstractRedmineMojo {

	/**
	 * @parameter default-value="true" expression="${redmine..allLowerVersions}"
	 */
	protected Boolean allLowerVersions;
	/**
	 * @parameter expression="${redmine.fromVersion}"
	 **/
	protected String fromVersion;
	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${redmine.toVersion}"
	 * @required true
	 **/
	protected String toVersion;

	private static Integer queryId = null;

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

		if (getLog().isInfoEnabled()) {
			getLog().info("Using projectId " + projectId);
		}

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			Project p = mgr.getProjectByKey(projectId);
			List<Issue> issues = mgr.getIssues(projectId, queryId);

			List<Version> prjectVersion = mgr.getVersions(p.getId());
			Version knownVersion = null;
			for (Version vers : prjectVersion) {
				if (vers.getName().compareTo(this.toVersion) == 0) {
					knownVersion = vers;
					break;
				}
			}
			if (knownVersion != null) {
				DefaultArtifactVersion v = new DefaultArtifactVersion(toVersion);
				for (Issue issue : issues) {
					System.out.println(issue.toString());
					if (allLowerVersions) {
						DefaultArtifactVersion vi = new DefaultArtifactVersion(
								issue.getTargetVersion().getName());
						if (vi != null) {
							if (vi.compareTo(v) == -1) {
								issue.setTargetVersion(knownVersion);
								mgr.update(issue);
							}
						}
					} else if (fromVersion != null) {
						if (issue.getTargetVersion().getName()
								.compareTo(fromVersion) == 0) {
							issue.setTargetVersion(knownVersion);
							mgr.update(issue);
						}
					} else {

					}
				}
			} else {
				// error
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package org.aferre.maven.redmine.plugin.versions;

import java.net.URL;
import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.aferre.maven.redmine.plugin.issues.MoveIssuesMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

/**
 * 
 * @goal close-versions
 * 
 */
@Mojo(name = "close-versions")
public class CloseVersionsMojo extends AbstractRedmineVersionsMojo {

	protected CloseVersionsMojo() {
		super();
	}

	protected CloseVersionsMojo(MavenProject mavenProject, URL hostUrl,
			String apiKey, Boolean dryRun, Boolean abortOnError,
			String projectId, Boolean interactive, String[] projectIds,
			Boolean all) {
		super(mavenProject, hostUrl, apiKey, dryRun, abortOnError, projectId,
				interactive, projectIds, all);
	}

	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${redmine.versionToBeClosed}"
	 */
	public String version;

	/**
	 * @parameter expression="${redmine.nextVersionForIssues}"
	 *            default-value=null
	 */
	public String nextVersionForIssues;

	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${redmine.moveOrphanedIssues}"
	 */
	private boolean moveOrphans;

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		if (projectIds != null && projectIds.length != 0) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for projects " + projectIds);
			}
			for (String projectName : projectIds) {
				try {
					closeVersion(mgr, projectName);
				} catch (Exception e) {
					if (getLog().isErrorEnabled()) {
						getLog().error(e.getMessage());
					}
				}
			}

		} else if (projectId != null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for project " + projectId);
			}
			try {
				closeVersion(mgr, projectId);
			} catch (Exception e) {
				if (getLog().isErrorEnabled()) {
					getLog().error(e.getMessage());
				}
			}

		} else {
			if (getLog().isErrorEnabled()) {
				getLog().error("No projectId(s) provided.");
			}
		}

	}

	public List<Version> getVersions(RedmineManager mgr, String projectName)
			throws RedmineException {
		Project project = mgr.getProjectByKey(projectName);
		List<Version> versions = getVersions(mgr, project);
		return versions;
	}

	private void closeVersion(RedmineManager mgr, String projectName)
			throws RedmineException {
		Project project = mgr.getProjectByKey(projectName);
		List<Version> versions = getVersions(mgr, project);
		Version versionToBeClosed = null;
		Version vnextForIssues = null;
		String toBeCLosed = Utils.getVersion(this.version);
		for (Version version : versions) {
			String name = version.getName();
			if (name.equals(toBeCLosed)) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version " + name + " found, closing it.");
				}
				versionToBeClosed = version;
			} else if (nextVersionForIssues != null
					&& name.equals(nextVersionForIssues)) {
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Version " + name
									+ " for nextVersionForIssues found.");
				}
				vnextForIssues = version;
			}
		}
		if (versionToBeClosed == null) {
			if (getLog().isErrorEnabled()) {
				getLog().error("Version does not exist.");
			}
			if (abortOnError) {

			}
		} else {
			if (versionToBeClosed.getStatus() != null
					&& versionToBeClosed.getStatus().equals("closed")) {
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Version "
									+ versionToBeClosed.getName()
									+ " already closed, checking if issues are correctly moved.");
				}
				if (this.nextVersionForIssues != null
						&& !this.nextVersionForIssues.isEmpty()) {
					if (vnextForIssues == null) {
						if (getLog().isErrorEnabled()) {
							getLog().error(
									"Targeted version " + nextVersionForIssues
											+ " does not exist.");
						}
						if (abortOnError) {

						}
					} else {
						if (getLog().isInfoEnabled()) {
							getLog().info(
									"Trying to move issues to version =>  "
											+ Utils.toString(vnextForIssues));
						}
						moveIssues(mgr, project, versionToBeClosed,
								vnextForIssues, moveOrphans);
					}
				}
			} else {
				versionToBeClosed.setStatus("closed");
				mgr.update(versionToBeClosed);
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Closed version " + toBeCLosed
									+ ", checking against server.");
				}
				versions = getVersions(mgr, project);
				versionToBeClosed = null;
				for (Version version : versions) {
					String name = version.getName();
					if (name.equals(toBeCLosed)) {
						if (getLog().isInfoEnabled()) {
							getLog().info("Version  " + name + " found.");
						}
						versionToBeClosed = version;
						break;
					}
				}
				if (versionToBeClosed == null) {
					if (getLog().isInfoEnabled()) {
						getLog().info(
								"Version does not exist after being closed :o");
					}
					if (abortOnError) {

					}
				} else {
					if (versionToBeClosed.getStatus() != null
							&& versionToBeClosed.getStatus().equals("closed")) {
						if (getLog().isInfoEnabled()) {
							getLog().info("Succesfully closed version");
						}

						if (this.nextVersionForIssues != null
								&& !this.nextVersionForIssues.isEmpty()) {
							if (vnextForIssues == null) {
								if (getLog().isErrorEnabled()) {
									getLog().error(
											"Targeted version "
													+ nextVersionForIssues
													+ " does not exist.");
								}
								if (abortOnError) {

								}
							} else {
								if (getLog().isErrorEnabled()) {
									getLog().error(
											"Moving issues to "
													+ Utils.toString(vnextForIssues));
								}
								moveIssues(mgr, project, versionToBeClosed,
										vnextForIssues, moveOrphans);
							}
						}
					} else {
						if (getLog().isInfoEnabled()) {
							getLog().info(
									"Something went wrong, status is now "
											+ versionToBeClosed.getStatus());
						}
					}

				}
			}
		}

	}

	private void moveIssues(RedmineManager mgr, Project proj,
			Version fromVersion, Version moveTo, boolean moveOrphans)
			throws RedmineException {

		MoveIssuesMojo mojo = new MoveIssuesMojo((AbstractRedmineMojo) this);

		List<Issue> issues = mgr.getIssues(proj.getIdentifier(), null);

		mojo.moveIssues(mgr, proj, issues, fromVersion, moveTo, moveOrphans,
				false);

	}

}

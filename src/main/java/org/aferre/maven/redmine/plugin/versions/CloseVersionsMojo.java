package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

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
	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${versionToBeClosed}"
	 */
	public String version;

	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${nextVersionForIssues}"
	 */
	public String nextVersionForIssues;

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
		Version known = null;
		Version vclosed = null;
		Version vnextForIssues = null;
		String toBeCLosed = Utils.getVersion(this.version);
		for (Version version : versions) {
			String name = version.getName();
			if (name.equals(toBeCLosed)) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version " + name + " found, closing it.");
				}
				known = version;
			} else if (name.equals(nextVersionForIssues)) {
				vnextForIssues = version;
			}
		}
		if (known == null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Version does not exist.");
			}
			if (abortOnError) {

			}
		} else {
			if (known.getStatus() != null && known.getStatus().equals("closed")) {
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Version " + known.getName() + " already closed.");
				}
			} else {
				vclosed = known;
				known.setStatus("closed");
				mgr.update(known);
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Closed version " + toBeCLosed
									+ ", checking against server.");
				}
				versions = getVersions(mgr, project);
				known = null;
				for (Version version : versions) {
					String name = version.getName();
					if (name.equals(toBeCLosed)) {
						if (getLog().isInfoEnabled()) {
							getLog().info("Version  " + name + " found.");
						}
						known = version;
						break;
					}
				}
				if (known == null) {
					if (getLog().isInfoEnabled()) {
						getLog().info(
								"Version does not exist after being closed :o");
					}
					if (abortOnError) {

					}
				} else {
					if (known.getStatus() != null
							&& known.getStatus().equals("closed")) {
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
								moveIssues(mgr, project, vclosed,
										vnextForIssues);
							}
						}
					} else {
						if (getLog().isInfoEnabled()) {
							getLog().info(
									"Something went wrong, status is now "
											+ known.getStatus());
						}
					}

				}
			}
		}

	}

	private void moveIssues(RedmineManager mgr, Project proj, Version vclosed,
			Version moveTo) throws RedmineException {
		List<Issue> issues = mgr.getIssues(proj.getIdentifier(), null);

		for (Issue issue : issues) {
			if (issue.getTargetVersion() != null) {
				if (issue.getTargetVersion().getName()
						.equals(vclosed.getName())) {

					String statusName = issue.getStatusName();
					if (statusName.equals("Closed")
							|| statusName.equals("Rejected")) {

						if (getLog().isDebugEnabled()) {
							getLog().debug(
									"Issue is " + statusName
											+ ", not moving it.");
						}

					} else {
						issue.setTargetVersion(moveTo);
						if (getLog().isDebugEnabled()) {
							getLog().debug("Moved issue.");
						}
						mgr.update(issue);
					}
				}
			} else {
				issue.setTargetVersion(moveTo);
				if (getLog().isDebugEnabled()) {
					getLog().debug(
							"Moved issue which didn't have a target version.");
				}
				mgr.update(issue);
			}
		}
	}

	private void createVersion(RedmineManager mgr, String projectName)
			throws RedmineException {
		Project project = mgr.getProjectByKey(projectName);
		List<Version> versions = getVersions(mgr, project);
		Version known = null;
		for (Version version : versions) {
			String name = version.getName();
			if (name.equals(Utils.getVersion(this.version))) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version  " + name + " already exists:");
					getLog().info(Utils.toString(version));
				}
				known = version;
				break;
			}
		}
		if (known == null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Version does not exist.");
			}
			Version v = new Version(project, Utils.getVersion(version));
			Version createVersion = mgr.createVersion(v);

			if (getLog().isInfoEnabled()) {
				getLog().info("Created version: ");
				getLog().info(Utils.toString(createVersion));
			}
		}
	}

}

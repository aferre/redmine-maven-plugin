package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
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
		String toBeCLosed = Utils.getVersion(this.version);
		for (Version version : versions) {
			String name = version.getName();
			if (name.equals(toBeCLosed)) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version " + name + " found closing it.");
				}
				known = version;
				break;
			}
		}
		if (known == null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Version does not exist.");
			}
		} else {
			if (known.getStatus() != null && known.getStatus().equals("closed")) {
				if (getLog().isInfoEnabled()) {
					getLog().info(
							"Version " + known.getName() + " already closed.");
				}
			} else {
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
				} else {
					if (known.getStatus() != null
							&& known.getStatus().equals("closed")) {
						if (getLog().isInfoEnabled()) {
							getLog().info("Succesfully closed version");
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

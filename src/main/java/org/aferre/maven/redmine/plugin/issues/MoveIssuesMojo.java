package org.aferre.maven.redmine.plugin.issues;

import java.util.ArrayList;
import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

/**
 * 
 * @goal move-issues
 * 
 */
/**
 * @author aferre
 * 
 */
@Mojo(name = "move-issues")
public class MoveIssuesMojo extends AbstractRedmineMojo {

	/**
	 * @parameter default-value="true" expression="${redmine.allLowerVersions}"
	 */
	private Boolean allLowerVersions;

	/**
	 * @parameter default-value="false"
	 *            expression="${redmine.moveOrphanedIssues}"
	 */
	private Boolean moveOrphans;

	/**
	 * @parameter expression="${redmine.fromVersion}"
	 **/
	private String fromVersion;

	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${redmine.toVersion}"
	 * @required true
	 **/
	private String toVersion;

	private static Integer queryId = null;

	public MoveIssuesMojo(AbstractRedmineMojo abstractRedmineMojo) {
		super(abstractRedmineMojo);
	}

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
		} else if (getAllLowerVersions() && getFromVersion() != null) {
			if (getLog().isErrorEnabled()) {
				getLog().error(
						"AllLowerVersions and fromVersion are both defined!");
			}
			return;
		}

		if (getLog().isInfoEnabled()) {
			getLog().info("Using projectId " + projectId);
		}

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			Project project = mgr.getProjectByKey(projectId);
			List<Issue> issues = getIssuesForProject(mgr, projectId);

			List<Version> prjectVersion = getVersionsForProject(mgr, project,
					false);

			Version fromVersion = null;
			String version = Utils.getVersion(this.getToVersion());
			Version toVersion = getVersion(prjectVersion, version);

			if (toVersion != null) {
				moveIssues(mgr, project, issues, fromVersion, toVersion,
						getMoveOrphans(), getAllLowerVersions());
			} else {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version " + version + " not found.");
				}
			}
		} catch (NotFoundException e) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Project " + projectId + "not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Parameters allLowerVersions and fromVersion are not to be used at the
	 * same time!
	 * 
	 * @param mgr
	 * @param thisProject
	 *            , the project for which issues are to be moved
	 * 
	 * @param issues
	 *            , the issues which need to be moveds
	 * 
	 * @param fromVersion
	 *            , the version from which the issues will be moved
	 * 
	 * @param toVersion
	 *            , the version the issues will be moved to
	 * 
	 * @param moveOrphanedIssues
	 *            , if true, the issues will be moved to the version toVersion
	 * 
	 * @param allLowerVersions
	 *            , if true, all issues in the issues list will be moved if they
	 *            have a lower version than toVersion
	 * 
	 * @throws RedmineException
	 */
	public void moveIssues(RedmineManager mgr, Project thisProject,
			List<Issue> issues, Version fromVersion, Version toVersion,
			boolean moveOrphanedIssues, boolean allLowerVersions)
			throws RedmineException {

		for (Issue issue : issues) {
			getLog().info(Utils.toString(issue));
			if (issue.getTargetVersion() != null) {

				Project issueProject = issue.getTargetVersion().getProject();
				if (issueProject != null) {
					if (issueProject.getName().equals(thisProject.getName())
							|| issueProject.getId() == thisProject.getId()) {
						if (allLowerVersions) {
							if (compareVersions(toVersion,
									issue.getTargetVersion()) == 1) {
								reallyMoveIssue(mgr, toVersion, issue);
							}
						} else if (fromVersion != null) {
							if (compareVersions(
									Utils.getVersion(issue.getTargetVersion()),
									Utils.getVersion(fromVersion)) == 0) {
								reallyMoveIssue(mgr, toVersion, issue);
							}
						} else {

						}
					} else {
						getLog().info("Issue from parent/child project");
					}
				} else {
					getLog().info("No id or name for project.");
				}
			} else {

				/*
				 * Moving orphans
				 */
				if (moveOrphanedIssues) {
					getLog().info("Moving orphaned issue.");
					reallyMoveIssue(mgr, toVersion, issue);
				}
			}
		}
	}

	private void reallyMoveIssue(RedmineManager mgr, Version toVersion,
			Issue issue) throws RedmineException {
		String statusName = issue.getStatusName();
		if (statusName.equals("Closed") || statusName.equals("Rejected")) {

			if (getLog().isDebugEnabled()) {
				getLog().debug("Issue is " + statusName + ", not moving it.");
			}
		} else {
			issue.setTargetVersion(toVersion);
			getLog().info("Moving issue.");
			mgr.update(issue);
			getLog().info("Moved issue.");
		}
	}

	private List<Issue> getIssuesForProject(RedmineManager mgr,
			String projectIdentifier) throws RedmineException {
		List<Issue> issues = mgr.getIssues(projectIdentifier, queryId);
		// for (Issue issue : issues) {
		// issue.getProject().getId();
		// }
		return issues;
	}

	private int compareVersions(Version version1, Version version2) {
		return compareVersions(version1.getName(), version2.getName());
	}

	private int compareVersions(String version1, String version2) {
		DefaultArtifactVersion v1 = new DefaultArtifactVersion(version1);
		DefaultArtifactVersion v2 = new DefaultArtifactVersion(version2);
		return v1.compareTo(v2);
	}

	private Version getVersion(List<Version> prjectVersion, String version) {
		for (Version vers : prjectVersion) {
			if (vers.getName().compareTo(version) == 0) {
				return vers;
			}
		}
		return null;
	}

	/**
	 * @param mgr
	 * @param p
	 * @param includeParentProjects
	 *            , if true, redmine parent projects' version will also be added
	 *            to the returned list.
	 * @return
	 * @throws RedmineException
	 */
	private List<Version> getVersionsForProject(RedmineManager mgr, Project p,
			boolean includeParentProjects) throws RedmineException {
		List<Version> prjectVersion = mgr.getVersions(p.getId());

		if (includeParentProjects) {
			return prjectVersion;
		} else {
			List<Version> ret = new ArrayList<Version>();

			for (Version vers : prjectVersion) {
				if (vers.getProject().getName().compareTo(p.getName()) == 0) {
					ret.add(vers);
				}
			}
			return ret;
		}
	}

	public Boolean getAllLowerVersions() {
		return allLowerVersions;
	}

	public void setAllLowerVersions(Boolean allLowerVersions) {
		this.allLowerVersions = allLowerVersions;
	}

	public Boolean getMoveOrphans() {
		return moveOrphans;
	}

	public void setMoveOrphans(Boolean moveOrphans) {
		this.moveOrphans = moveOrphans;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}
}

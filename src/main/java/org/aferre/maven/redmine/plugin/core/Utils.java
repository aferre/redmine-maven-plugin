package org.aferre.maven.redmine.plugin.core;

import org.apache.maven.project.MavenProject;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;

public class Utils {
	public static void printVersion(Version version) {
		String stringBuilder = toString(version);
		System.out.println(stringBuilder);
	}

	public static String toString(Issue issue) {
		if (issue == null)
			return "null";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(issue.toString());
		stringBuilder.append(", targeted version: ");
		stringBuilder.append(Utils.toString(issue.getTargetVersion()));
		return stringBuilder.toString();
	}

	public static String toString(Version version) {
		if (version == null)
			return "null";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Version name: ");
		stringBuilder.append(version.getName());
		stringBuilder.append(", Project: ");
		stringBuilder.append(version.getProject());
		stringBuilder.append(", Status: ");
		stringBuilder.append(version.getStatus());
		stringBuilder.append(", Id: ");
		stringBuilder.append(version.getId());
		stringBuilder.append(", CreatedOn: ");
		stringBuilder.append(version.getCreatedOn());
		stringBuilder.append(", Description: ");
		stringBuilder.append(version.getDescription());
		stringBuilder.append(", DueDate: ");
		stringBuilder.append(version.getDueDate());
		stringBuilder.append(", UpdatedOn: ");
		stringBuilder.append(version.getUpdatedOn());
		return stringBuilder.toString();
	}

	public static void printProject(Project issue) {
		String stringBuilder = toString(issue);
		System.out.println(stringBuilder);
	}

	public static String toString(Tracker tracker) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID: " + tracker.getId());
		stringBuilder.append(", Name: " + tracker.getName());
		return stringBuilder.toString();
	}

	public static String getProjectVersion(MavenProject project) {
		return getVersion(project.getVersion());
	}

	public static String getVersion(String project) {
		String version = project;

		if (version == null) {
			throw new NullPointerException("No version");
		}
		if (version.contains("-SNAPSHOT")) {
			return new String(version.replace("-SNAPSHOT", ""));
		}
		return version;
	}

	public static String toString(Project project) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Project Name : ");
		stringBuilder.append(project.getName());
		stringBuilder.append(", Identifier : ");
		stringBuilder.append(project.getIdentifier());
		stringBuilder.append(", Id : ");
		stringBuilder.append(project.getId());
		stringBuilder.append(", Description : ");
		stringBuilder.append(project.getDescription());
		stringBuilder.append(", CreatedOn : ");
		stringBuilder.append(project.getCreatedOn());
		stringBuilder.append(", Homepage : ");
		stringBuilder.append(project.getHomepage());
		stringBuilder.append(", ParentId : ");
		stringBuilder.append(project.getParentId());
		stringBuilder.append(", Trackers : [");
		if (project.getTrackers() != null) {
			for (Tracker tracker : project.getTrackers()) {
				stringBuilder.append(Utils.toString(tracker) + ",");
			}
		}
		stringBuilder.append("], UpdatedOn : ");
		stringBuilder.append(project.getUpdatedOn());
		return stringBuilder.toString();
	}

	public static void printUser(User user) {
		System.out.println("FullName : " + user.getFullName());
		System.out.println("CreatedOn : " + user.getCreatedOn());
		System.out.println("Login : " + user.getLogin());
		System.out.println("Mail : " + user.getMail());
		System.out.println("Password : " + user.getPassword());
		System.out.println("Id : " + user.getId());
		System.out.println("LastLoginOn : " + user.getLastLoginOn());
		System.out.println("Memberships : " + user.getMemberships());
		System.out.println("CustomFields : " + user.getCustomFields());
	}
}

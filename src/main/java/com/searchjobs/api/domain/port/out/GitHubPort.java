package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserProject;
import java.util.List;

public interface GitHubPort {
    List<UserProject> fetchProjects(String githubUsername);
}
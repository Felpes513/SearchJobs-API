package com.searchjobs.api.application.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JSearchResponseDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private List<JobDataDto> data;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JobDataDto {

        @JsonProperty("job_id")
        private String jobId;

        @JsonProperty("job_title")
        private String jobTitle;

        @JsonProperty("employer_name")
        private String employerName;

        @JsonProperty("job_city")
        private String jobCity;

        @JsonProperty("job_state")
        private String jobState;

        @JsonProperty("job_country")
        private String jobCountry;

        @JsonProperty("job_is_remote")
        private Boolean jobIsRemote;

        @JsonProperty("job_description")
        private String jobDescription;

        @JsonProperty("job_min_salary")
        private Double jobMinSalary;

        @JsonProperty("job_max_salary")
        private Double jobMaxSalary;

        @JsonProperty("job_salary_currency")
        private String jobSalaryCurrency;

        @JsonProperty("job_apply_link")
        private String jobApplyLink;

        @JsonProperty("job_posted_at_datetime_utc")
        private String jobPostedAtDatetimeUtc;

        @JsonProperty("job_employment_type")
        private String jobEmploymentType;

        @JsonProperty("job_required_skills")
        private List<String> jobRequiredSkills;

        public String getLocalizacao() {
            if (jobIsRemote != null && jobIsRemote) return "Remoto";
            StringBuilder sb = new StringBuilder();
            if (jobCity != null) sb.append(jobCity);
            if (jobState != null) sb.append(", ").append(jobState);
            if (jobCountry != null) sb.append(", ").append(jobCountry);
            return sb.toString();
        }

        public String getSalario() {
            if (jobMinSalary == null && jobMaxSalary == null) return null;
            String currency = jobSalaryCurrency != null ? jobSalaryCurrency : "";
            if (jobMinSalary != null && jobMaxSalary != null) {
                return currency + " " + jobMinSalary.intValue() + " - " + jobMaxSalary.intValue();
            }
            if (jobMinSalary != null) return currency + " " + jobMinSalary.intValue() + "+";
            return currency + " até " + jobMaxSalary.intValue();
        }
    }
}
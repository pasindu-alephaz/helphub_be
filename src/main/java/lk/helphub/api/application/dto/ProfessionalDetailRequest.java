package lk.helphub.api.application.dto;

import java.util.List;
import java.util.UUID;

public class ProfessionalDetailRequest {
    private String skills;
    private String experience;
    private List<UUID> categoryIds;

    public ProfessionalDetailRequest() {}

    public ProfessionalDetailRequest(String skills, String experience, List<UUID> categoryIds) {
        this.skills = skills;
        this.experience = experience;
        this.categoryIds = categoryIds;
    }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public List<UUID> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<UUID> categoryIds) { this.categoryIds = categoryIds; }

    public static ProfessionalDetailRequestBuilder builder() {
        return new ProfessionalDetailRequestBuilder();
    }

    public static class ProfessionalDetailRequestBuilder {
        private String skills;
        private String experience;
        private List<UUID> categoryIds;

        public ProfessionalDetailRequestBuilder skills(String skills) { this.skills = skills; return this; }
        public ProfessionalDetailRequestBuilder experience(String experience) { this.experience = experience; return this; }
        public ProfessionalDetailRequestBuilder categoryIds(List<UUID> categoryIds) { this.categoryIds = categoryIds; return this; }

        public ProfessionalDetailRequest build() {
            return new ProfessionalDetailRequest(skills, experience, categoryIds);
        }
    }
}

package lk.helphub.api.application.dto;

import java.util.List;
import java.util.UUID;

public class UserProfessionalDetailResponse {
    private UUID id;
    private String skills;
    private String experience;
    private List<CategoryResponse> categories;

    public UserProfessionalDetailResponse() {}

    public UserProfessionalDetailResponse(UUID id, String skills, String experience, List<CategoryResponse> categories) {
        this.id = id;
        this.skills = skills;
        this.experience = experience;
        this.categories = categories;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public List<CategoryResponse> getCategories() { return categories; }
    public void setCategories(List<CategoryResponse> categories) { this.categories = categories; }

    public static UserProfessionalDetailResponseBuilder builder() {
        return new UserProfessionalDetailResponseBuilder();
    }

    public static class UserProfessionalDetailResponseBuilder {
        private UUID id;
        private String skills;
        private String experience;
        private List<CategoryResponse> categories;

        public UserProfessionalDetailResponseBuilder id(UUID id) { this.id = id; return this; }
        public UserProfessionalDetailResponseBuilder skills(String skills) { this.skills = skills; return this; }
        public UserProfessionalDetailResponseBuilder experience(String experience) { this.experience = experience; return this; }
        public UserProfessionalDetailResponseBuilder categories(List<CategoryResponse> categories) { this.categories = categories; return this; }

        public UserProfessionalDetailResponse build() {
            return new UserProfessionalDetailResponse(id, skills, experience, categories);
        }
    }
}

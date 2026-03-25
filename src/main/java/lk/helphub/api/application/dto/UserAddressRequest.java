package lk.helphub.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request to add or update a user address")
public class UserAddressRequest {

    @NotBlank(message = "Label is required (e.g. Home, Work)")
    @Size(max = 50)
    @Schema(description = "Label for this address", example = "Home", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @Schema(description = "Province", example = "Western")
    private String province;

    @Schema(description = "District", example = "Colombo")
    private String district;

    @Schema(description = "City", example = "Colombo 03")
    private String city;

    @Schema(description = "Postal / zip code", example = "00300")
    private String postalCode;

    @Schema(description = "GPS latitude", example = "6.9271")
    private BigDecimal latitude;

    @Schema(description = "GPS longitude", example = "79.8612")
    private BigDecimal longitude;

    @Schema(description = "Set as default address for job requests", example = "true")
    private boolean isDefault;

    public UserAddressRequest() {}

    public UserAddressRequest(String label, String province, String district, String city, String postalCode, BigDecimal latitude, BigDecimal longitude, boolean isDefault) {
        this.label = label;
        this.province = province;
        this.district = district;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public static UserAddressRequestBuilder builder() {
        return new UserAddressRequestBuilder();
    }

    public static class UserAddressRequestBuilder {
        private String label;
        private String province;
        private String district;
        private String city;
        private String postalCode;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private boolean isDefault;

        public UserAddressRequestBuilder label(String label) { this.label = label; return this; }
        public UserAddressRequestBuilder province(String province) { this.province = province; return this; }
        public UserAddressRequestBuilder district(String district) { this.district = district; return this; }
        public UserAddressRequestBuilder city(String city) { this.city = city; return this; }
        public UserAddressRequestBuilder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public UserAddressRequestBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public UserAddressRequestBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }
        public UserAddressRequestBuilder isDefault(boolean isDefault) { this.isDefault = isDefault; return this; }

        public UserAddressRequest build() {
            return new UserAddressRequest(label, province, district, city, postalCode, latitude, longitude, isDefault);
        }
    }
}

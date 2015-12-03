package io.paradoxical.francois.jenkins.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PromotionList {

    @JsonProperty("processes")
    private final List<JobPromotion> promotions;

    @JsonCreator
    public PromotionList(@JsonProperty("processes") final List<JobPromotion> promotions) {
        this.promotions = promotions;
    }
}

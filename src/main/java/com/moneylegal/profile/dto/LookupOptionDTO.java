package com.moneylegal.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupOptionDTO {
    private String id;
    private String label;
    private String icon; // emoji ou nome do Lucide (string)
}

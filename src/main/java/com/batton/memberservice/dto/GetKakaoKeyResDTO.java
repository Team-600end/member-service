package com.batton.memberservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GetKakaoKeyResDTO {
    private String key;
    private String redirect;

    @Builder
    public GetKakaoKeyResDTO(String key, String redirect) {
        this.key = key;
        this.redirect = redirect;
    }

    public static GetKakaoKeyResDTO toDTO(String key, String redirect) {
        return GetKakaoKeyResDTO.builder()
                .key(key)
                .redirect(redirect)
                .build();
    }
}

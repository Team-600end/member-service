package com.batton.memberservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostEmailReqDTO {
    private String email;

    @Builder
    public PostEmailReqDTO(String email) {
        this.email = email;
    }
}

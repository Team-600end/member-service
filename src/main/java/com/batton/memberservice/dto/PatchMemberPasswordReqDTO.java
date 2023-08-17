package com.batton.memberservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchMemberPasswordReqDTO {
    private String currentPassword;
    private String changedPassword;
    private String checkChangedPassword;

    @Builder
    public PatchMemberPasswordReqDTO(String currentPassword, String changedPassword, String checkChangedPassword) {
         this.currentPassword=currentPassword;
         this.changedPassword=changedPassword;
         this.checkChangedPassword=checkChangedPassword;
    }
}

package com.moneylegal.profile.dto;

import com.moneylegal.profile.model.Profile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChooseTypeRequestDTO {
    
    @NotNull(message = "Tipo de cadastro é obrigatório")
    private Profile.TipoCadastro tipo;
}

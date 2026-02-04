package org.gfg.DigitalLibrary.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.DigitalLibrary.model.Address;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreationRequest {

    @NotNull
//    @Length(min = 2, max = 18)
    String name;
    @NotNull
    String email;
    @NotNull
    String mobileNumber;
    @NotNull
    Address address;
    @NotNull
    String dob;

}

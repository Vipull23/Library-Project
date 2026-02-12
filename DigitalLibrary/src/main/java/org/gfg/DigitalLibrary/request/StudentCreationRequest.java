package org.gfg.DigitalLibrary.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.DigitalLibrary.annotations.ValidAge;
import org.gfg.DigitalLibrary.model.Address;

import java.time.LocalDate;


@Data
@NoArgsConstructor
public class StudentCreationRequest {

    @NotNull
    String name;
    @NotNull
    String email;
    @NotNull
    String mobileNumber;
    @NotNull
    Address address;
    @NotNull
    @ValidAge
    LocalDate dob;
    //String dob;

}

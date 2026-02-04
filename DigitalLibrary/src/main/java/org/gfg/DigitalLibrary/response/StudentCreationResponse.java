package org.gfg.DigitalLibrary.response;

import lombok.Data;

@Data
public class StudentCreationResponse extends Response {

    private Integer id;
    private String name;
    private String email;
}

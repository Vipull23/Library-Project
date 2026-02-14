package org.gfg.DigitalLibrary.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookCreationResponse extends Response {

     String bookName;

}

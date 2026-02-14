package org.gfg.DigitalLibrary.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookTransactionRequest {
    int studentId;
    int bookId;
    String amount;
    String requestType;
}

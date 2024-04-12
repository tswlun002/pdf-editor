package com.documentservice.email;

import com.documentservice.pdf.PDF;
import org.springframework.stereotype.Service;
@Service("doc-email")
public interface IEmail {

    boolean sendEmail(PDF doc);
}

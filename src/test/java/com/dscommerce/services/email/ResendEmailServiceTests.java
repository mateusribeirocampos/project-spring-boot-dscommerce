package com.dscommerce.services.email;

import com.dscommerce.dto.EmailDTO;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResendEmailServiceTests {

    @InjectMocks
    private ResendEmailService emailService;

    @Mock
    private Resend resend;

    @Mock
    private Emails emails;

    @Mock
    private CreateEmailResponse response;

    @Test
    public void shouldReturnPlainTextEmailWhenGetContentTypeIsTextPlain() throws ResendException {
        EmailDTO emailDTO = new EmailDTO(
                "from@test.com", "From Name", "reply@test.com",
                "to@test.com", "Subject", "Body", "text/plain"
        );
        Mockito.when(resend.emails()).thenReturn(emails);
        Mockito.when(emails.send(Mockito.any(CreateEmailOptions.class))).thenReturn(response);
        emailService.plainTextEmail(emailDTO);
        Mockito.verify(emails).send(Mockito.any(CreateEmailOptions.class));
    }

    @Test
    public void shouldReturnPlainTextEmailWhenGetContentTypeIsNotTextPlain() throws ResendException {
        EmailDTO emailDTO = new EmailDTO(
                "from@test.com", "From Name", "reply@test.com",
                "to@test.com", "Subject", "Body", "text/html"
        );
        Mockito.when(resend.emails()).thenReturn(emails);
        Mockito.when(emails.send(Mockito.any(CreateEmailOptions.class))).thenReturn(response);
        emailService.plainTextEmail(emailDTO);
        Mockito.verify(emails).send(Mockito.any(CreateEmailOptions.class));
    }

    @Test
    public void  shouldNotThrowExceptionWhenResendFails() throws ResendException {
        EmailDTO emailDTO = new EmailDTO(
                "from@test.com", "From Name", "reply@test.com",
                "to@test.com", "Subject", "Body", "text/plain"
        );
        Mockito.when(resend.emails()).thenReturn(emails);
        Mockito.when(emails.send(Mockito.any(CreateEmailOptions.class))).thenThrow(new ResendException("Erro simulado"));
        emailService.plainTextEmail(emailDTO);
        Mockito.verify(emails).send(Mockito.any(CreateEmailOptions.class));
    }

}

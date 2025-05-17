package com.wishtreetech.salesbridge.emailverification.controller;

import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.emailverification.entity.Person;
import com.wishtreetech.emailverification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling email verification requests.
 */
@RestController
@RequestMapping("/api/email")
public class EmailVerificationController {

    @Autowired
    private EmailService emailService;

    /**
     * Verifies multiple email addresses concurrently.
     * Requires the user to have the "VERIFY_EMAIL" authority.
     *
     * @param people A list of {@link Person} objects containing names and domains.
     * @return A {@link ResponseDto} containing a list of verified persons with their emails.
     */
    @PreAuthorize("hasAuthority('VERIFY_EMAIL')")
    @PostMapping("/verify/all")
    public ResponseDto<List<Person>> verifyEmails(@RequestBody List<Person> people) {
        return new ResponseDto<>(true, emailService.getVerifiedEmailsConcurrent(people));
    }

    /**
     * Verifies a single email address.
     * Requires the user to have the "VERIFY_EMAIL" authority.
     *
     * @param person A {@link Person} object containing the name and domain.
     * @return A {@link ResponseDto} containing the verified person with their emails.
     */
    @PreAuthorize("hasAuthority('VERIFY_EMAIL')")
    @PostMapping("/verify")
    public ResponseDto<Person> verifyEmail(@RequestBody Person person) {
        return new ResponseDto<>(true, emailService.getVerifiedEmail(person));
    }
}

package com.wishtreetech.emailverification.service.implementation;

import com.wishtreetech.emailverification.entity.Person;
import com.wishtreetech.emailverification.service.EmailService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000;

    /**
     * Generates possible email patterns based on name and domain.
     */
    private static final List<String> EMAIL_PATTERNS = Arrays.asList(
            "{first}@{domain}", "{last}@{domain}", "{first}.{last}@{domain}",
            "{first}{last}@{domain}", "{f}{last}@{domain}", "{first}{l}@{domain}",
            "{f}.{last}@{domain}", "{f}_{last}@{domain}", "{last}.{first}@{domain}",
            "{last}{f}@{domain}", "{f}{l}@{domain}"
    );

    /**
     * Generates email patterns based on the given full name and domain.
     *
     * @param fullName The full name of the person.
     * @param domain   The email domain.
     * @return A list of possible email addresses.
     */
    public List<String> generateEmailPatterns(String fullName, String domain) {
        fullName = fullName.trim().toLowerCase();
        domain = domain.trim().toLowerCase();
        String[] nameParts = fullName.split("\\s+");

        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must include at least first and last name.");
        }

        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        char f = firstName.charAt(0);
        char l = lastName.charAt(0);

        String finalDomain = domain;
        return EMAIL_PATTERNS.stream()
                .map(pattern -> pattern.replace("{first}", firstName)
                        .replace("{last}", lastName)
                        .replace("{f}", String.valueOf(f))
                        .replace("{l}", String.valueOf(l))
                        .replace("{domain}", finalDomain))
                .collect(Collectors.toList());
    }

    /**
     * Performs an MX record lookup to find the mail server for the given domain.
     *
     * @param domain The email domain.
     * @return The mail server address or null if not found.
     */
    private String getMailServer(String domain) {
        try {
            Record[] records = new Lookup(domain, Type.MX).run();
            if (records == null || records.length == 0) {
                return null;
            }

            Arrays.sort(records, Comparator.comparingInt(r -> ((MXRecord) r).getPriority()));
            for (Record record : records) {
                String mailServer = ((MXRecord) record).getTarget().toString();
                if (mailServer != null && !mailServer.isEmpty()) {
                    return mailServer;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validates an email address via SMTP handshake.
     *
     * @param email The email address to validate.
     * @return True if the email is valid, false otherwise.
     */
    public boolean validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            return false;
        }
        String domain = email.substring(email.indexOf('@') + 1);
        String mailServer = getMailServer(domain);
        return mailServer != null && validateWithSMTP(email, mailServer);
    }

    /**
     * Validates an email address via SMTP handshake.
     * @param email
     * @param mailServer
     * @return
     */
    private boolean validateWithSMTP(String email, String mailServer) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try (Socket socket = new Socket(mailServer, 25)) {
                socket.setSoTimeout(CONNECTION_TIMEOUT);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String response = in.readLine();
                    if (response == null || !response.startsWith("220")) {
                        continue;
                    }

                    // Use a more professional domain for HELO
                    out.println("HELO verifier.example.com");
                    response = in.readLine();
                    if (!response.startsWith("250")) {
                        continue;
                    }

                    // Use a more stable sender email
                    out.println("MAIL FROM:<" + generateSenderEmail() + ">");
                    response = in.readLine();
                    if (!response.startsWith("250")) {
                        continue;
                    }

                    out.println("RCPT TO:<" + email + ">");
                    response = in.readLine();

                    out.println("QUIT");

                    if (response.startsWith("250")) {
                        return true;
                    }

                    // If we get a 550 response, the email definitely doesn't exist
                    if (response.startsWith("550")) {
                        return false;
                    }
                }
            } catch (Exception e) {
                System.err.println("SMTP verification attempt " + (attempt + 1) + " failed for " + email +
                        ": " + e.getMessage());

                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Generates a list of verified email addresses based on the given full name and domain.
     *
     * @param fullName
     * @param domain
     * @return
     */
    public List<String> getVerifiedEmails(String fullName, String domain) {
        return generateEmailPatterns(fullName, domain).stream().filter(this::validateEmail).toList();
    }

    /**
     * Gets the verified email address for the given person.
     *
     * @param person
     * @return
     */
    public Person getVerifiedEmail(Person person) {
        if (person == null) {
            return null;
        }
        List<Person> personList = getVerifiedEmailsConcurrent(List.of(person));
        return personList.get(0);
    }

    /**
     * Gets the verified email addresses for the given list of people.
     *
     * @param people
     * @return
     */
    public List<Person> getVerifiedEmailsConcurrent(List<Person> people) {
        if (people == null || people.isEmpty()) {
            return new ArrayList<>();
        }
        List<CompletableFuture<Person>> futures = people.stream().map(person -> CompletableFuture.supplyAsync(() -> {
            boolean isCatchAll = isCatchAllDomain(person.getDomain());
            List<String> verifiedEmails = isCatchAll ? List.of() : getVerifiedEmails(person.getName(), person.getDomain());
            return new Person(person.getName(), person.getDomain(), verifiedEmails, isCatchAll ? "Catch-all domain detected." : "");
        }, executor)).toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }

    public String generateSenderEmail() {
        return UUID.randomUUID().toString().substring(0, 10) + "@gmail.com";
    }

    public boolean isCatchAllDomain(String domain) {
        return validateEmail(UUID.randomUUID().toString().substring(0, 20) + "@" + domain);
    }
}
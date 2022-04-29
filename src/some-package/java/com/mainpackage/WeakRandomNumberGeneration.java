package com.mainpackage;

import java.security.SecureRandom;

/**
 * Even though this file contains weak crypto issues, CodeGuru Reviewer will not
 * report any issues in it, because it has been excluded in aws-codeguru-reviewer.yml.
 * 
 * For more information, see the Amazon CodeGuru Reviewer User Guide:
 * https://docs.aws.amazon.com/codeguru/latest/reviewer-ug/welcome.html.
 */
public class WeakRandomNumberGeneration {
    // {fact rule=weak-random-number-generation@v1.0 defects=1}
    static void secureRandomSpecificAlgorithmNoncompliant() throws Exception {
        final String ALGORITHM_NAME = "DES";
        // Noncompliant: one specific algorithm is requested.
        SecureRandom generator = SecureRandom.getInstance(ALGORITHM_NAME);
        System.out.println(generator.nextInt());
    }
    // {/fact}

    // {fact rule=weak-random-number-generation@v1.0 defects=0}
    static void secureRandomDefaultCompliant() throws Exception {
        // Compliant: no specific algorithm is requested.
        SecureRandom generator = new SecureRandom();
        System.out.println(generator.nextInt());
    }
    // {/fact}
}

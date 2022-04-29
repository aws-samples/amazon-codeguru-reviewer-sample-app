package com.mainpackage;

import javax.servlet.http.HttpServletRequest;

/**
 * Even though this file contains weak crypto issues, CodeGuru Reviewer will not
 * report any issues in it, because it has been excluded in aws-codeguru-reviewer.yml.
 * 
 * For more information, see the Amazon CodeGuru Reviewer User Guide:
 * https://docs.aws.amazon.com/codeguru/latest/reviewer-ug/welcome.html.
 */
public class SqlInjection {

    // {fact rule=sql-injection@v1.0 defects=1}
    public void executeSqlStatementNoncompliant(HttpServletRequest request, java.sql.Connection connection) {
        final String favoriteColor = request.getParameter("favoriteColor");
        try {
            String sql = "SELECT * FROM people WHERE favorite_color='" + favoriteColor + "'";
            java.sql.Statement statement = connection.createStatement();
            // Noncompliant: user-given input is not sanitized before use.
            statement.execute(sql);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // {/fact}

    // {fact rule=sql-injection@v1.0 defects=0}
    public void executeSqlStatementCompliant(HttpServletRequest request, java.sql.Connection connection) {
        final String favoriteColor = request.getParameter("favoriteColor");
        // Compliant: user-given input is sanitized before use.
        if (!favoriteColor.matches("[a-z]+")) {
            throw new IllegalArgumentException();
        }
        try {
            String sql = "SELECT * FROM people WHERE favorite_color='" + favoriteColor + "'";
            java.sql.Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // {/fact}

}

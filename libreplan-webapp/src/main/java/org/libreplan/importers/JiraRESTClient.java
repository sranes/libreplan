/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2013 St. Antoniusziekenhuis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.importers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.libreplan.importers.jira.Issue;
import org.libreplan.importers.jira.SearchResult;
import org.libreplan.ws.cert.NaiveTrustProvider;
import org.libreplan.ws.common.impl.Util;

/**
 * Client to interact with Jira RESTful web service.
 *
 * @author Miciele Ghiorghis <m.ghiorghis@antoniusziekenhuis.nl>
 */
public class JiraRESTClient {


    /**
     * Path for search operation in JIRA REST API
     */
    public static final String PATH_SEARCH = "rest/api/latest/search";

    /**
     * Path for authenticate session in JIRA REST API
     */
    public static final String PATH_AUTH_SESSION = "rest/auth/latest/session";

    /**
     * Path for issue operations in JIRA REST API
     */
    public static final String PATH_ISSUE = "/rest/api/latest/issue/";

    private static final MediaType[] mediaTypes = new MediaType[] {
            MediaType.valueOf(MediaType.APPLICATION_JSON),
            MediaType.valueOf(MediaType.APPLICATION_XML) };


    /**
     * Queries Jira for all labels
     *
     * @param url
     *            the url from where to fetch data
     * @return List of labels
     */
    public static List<String> getAllLables(String url) {
        WebClient client = WebClient.create(url).accept(mediaTypes);
        String labels = client.get(String.class);
        return Arrays.asList(StringUtils.split(labels, ","));
    }

    /**
     * Query Jira for all issues with the specified query parameter
     *
     * @param path
     *            the path segment
     * @param query
     *            the query
     * @return List of jira Issues
     */
    public static List<Issue> getIssues(String url, String username,
            String password, String path, String query) {

        WebClient client = createClient(url);

        checkAutherization(client, username, password);

        client.back(true);// Go to baseURI
        client.path(path);
        if (!query.isEmpty()) {
            client.query("jql", query);
            client.query("maxResults", 1000);
        }
        SearchResult searchResult = client.get(SearchResult.class);

        return getIssuesDetails(client, searchResult.getIssues());
    }

    /**
     * Creates WebClient
     *
     * @param url
     *            the url
     * @return the created WebClient
     */
    private static WebClient createClient(String url) {

        JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider();
        jacksonJaxbJsonProvider.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);

        return WebClient.create(url,
                Collections.singletonList(jacksonJaxbJsonProvider)).accept(
                mediaTypes);

    }

    /**
     * Request jira for authorization check
     *
     * @param client
     *            jira client
     * @param login
     *            login name
     * @param password
     *            login password
     */
    private static void checkAutherization(WebClient client, String login,
            String password) {
        NaiveTrustProvider.setAlwaysTrust(true);

        client.path(PATH_AUTH_SESSION);

        Util.addAuthorizationHeader(client, login, password);
        Response response = client.get();

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException("Authorization failed");
        }
    }

    /**
     * Iterate through issues and get issue details
     *
     * @param client
     *            the jira client
     * @param issues
     *            jira issues
     *
     * @return List of jira issue details
     */
    private static List<Issue> getIssuesDetails(WebClient client, List<Issue> issues) {

        client.back(true);
        client.path(PATH_ISSUE);

        List<Issue> issueDetails = new ArrayList<Issue>();
        for (Issue issue : issues) {
            issueDetails.add(client.path(issue.getId()).get(Issue.class));
            client.back(false);
        }
        return issueDetails;
    }
}

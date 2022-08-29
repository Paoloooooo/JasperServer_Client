package com.reportmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Paolo
 */
public class ResourceLookup {

    private final String creationDate;
    private final String description;
    private final String label;
    private final String permissionMask;
    private final String updateDate;
    private final String uri;
    private final String version;
    private final String resourceType;

    /**
     *
     * The constructor
     *
     * @param creationDate      the creation date
     * @param description       the description
     * @param label             the label
     * @param permissionMask    the permission mask
     * @param updateDate        the update date
     * @param uri               the uri
     * @param version           the version
     * @param resourceType      the resource type
     */
    public ResourceLookup(String creationDate, String description, String label, String permissionMask, String updateDate, String uri, String version, String resourceType) {

        this.creationDate = creationDate;
        this.description = description;
        this.label = label;
        this.permissionMask = permissionMask;
        this.updateDate = updateDate;
        this.uri = uri;
        this.version = version;
        this.resourceType = resourceType;
    }

    /**
     *
     * Gets the creation date
     *
     * @return the creation date
     */
    public String getCreationDate() {

        return creationDate;
    }

    /**
     *
     * Gets the description
     *
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     *
     * Gets the label
     *
     * @return the label
     */
    public String getLabel() {

        return label;
    }

    /**
     *
     * Gets the permission mask
     *
     * @return the permission mask
     */
    public String getPermissionMask() {

        return permissionMask;
    }

    /**
     *
     * Gets the update date
     *
     * @return the update date
     */
    public String getUpdateDate() {

        return updateDate;
    }

    /**
     *
     * Gets the uri
     *
     * @return the uri
     */
    public String getUri() {

        return uri;
    }

    /**
     *
     * Gets the version
     *
     * @return the version
     */
    public String getVersion() {

        return version;
    }

    /**
     *
     * Gets the resource type
     *
     * @return the resource type
     */
    public String getResourceType() {

        return resourceType;
    }

    @Override

    /**
     *
     * To string
     *
     * @return String
     */
    public String toString() {
        return "ResourceLookup{" + "creationDate=" + creationDate + ", description=" + description + ", label=" + label + ", permissionMask=" + permissionMask + ", updateDate=" + updateDate + ", uri=" + uri + ", version=" + version + ", resourceType=" + resourceType + '}';
    }

    /**
     *
     * Parses an input stream in order to generate a list of ResourceLookup
     *
     * @param in                        the inputStream to parse
     * @return List<ResourceLookup>     the list of ResourceLookup generated
     * @throws Exception
     */
    public static List<ResourceLookup> parse(InputStream in) throws IOException {

        List<ResourceLookup> list = new ArrayList<ResourceLookup>();

        try (Scanner s = new Scanner(in)) {
            String creationDate = "";
            String description = "";
            String label = "";
            String permissionMask = "";
            String updateDate = "";
            String uri = "";
            String version = "";
            String resourceType = "";

            while (s.hasNext()) {
                s.useDelimiter("<(\\/)?label>");
                s.next();
                label=s.next();

                s.useDelimiter("<(\\/)?uri>");
                s.next();
                uri=s.next();

                s.useDelimiter("<(\\/)?resourceType>");
                s.next();
                resourceType=s.next();

                s.useDelimiter("<(\\/)?resourceLookup>");
                s.next();
                list.add(new ResourceLookup(creationDate, description, label, permissionMask, updateDate, uri, version, resourceType));
            }
        }catch(NoSuchElementException ignored){

        }

        return list;
    }

    public static class SearchResult {

        private final List<ResourceLookup> list;
        private final int responseCode;

        /**
         *
         * The constructor
         *
         * @param list          the list
         * @param responseCode  the response code
         */
        public SearchResult(List<ResourceLookup> list, int responseCode) {

            this.list = list;
            this.responseCode = responseCode;
        }

        /**
         *
         * The constructor
         *
         * @param responseCode the response code
         */
        public SearchResult(int responseCode) {

            this.list = null;
            this.responseCode = responseCode;
        }

        /**
         *
         * Gets the list
         *
         * @return the list
         */
        public List<ResourceLookup> getList() {

            return list;
        }

        /**
         *
         * Gets the response code
         *
         * @return the response code
         */
        public int getResponseCode() {

            return responseCode;
        }
    }
}

package seedu.addressbook;

public final class Message {

	private Message() {}
	
	/*
     * ==============NOTE TO STUDENTS======================================
     * These messages shown to the user are defined in one place for convenient
     * editing and proof reading. Such messages are considered part of the UI
     * and may be subjected to review by UI experts or technical writers. Note
     * that Some of the strings below include '%1$s' etc to mark the locations
     * at which java String.format(...) method can insert values.
     * ====================================================================
     */
	
    public static final String ADDED = "New person added: %1$s, Phone: %2$s, Email: %3$s";
    public static final String ADDRESSBOOK_CLEARED = "Address book has been cleared!";
    public static final String COMMAND_HELP = "%1$s: %2$s";
    public static final String COMMAND_HELP_PARAMETERS = "\tParameters: %1$s";
    public static final String COMMAND_HELP_EXAMPLE = "\tExample: %1$s";
    public static final String DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String DISPLAY_PERSON_DATA = "%1$s  Phone Number: %2$s  Email: %3$s";
    public static final String DISPLAY_LIST_ELEMENT_INDEX = "%1$d. ";
    public static final String GOODBYE = "Exiting Address Book... Good bye!";
    public static final String INVALID_COMMAND_FORMAT = "Invalid command format: %1$s " + AddressBook.LS + "%2$s";
    public static final String INVALID_FILE = "The given file name [%1$s] is not a valid file name!";
    public static final String INVALID_PROGRAM_ARGS = "Too many parameters! Correct program argument format:"
                                                            + AddressBook.LS + "\tjava AddressBook"
                                                            + AddressBook.LS + "\tjava AddressBook [custom storage file path]";
    public static final String INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String INVALID_STORAGE_FILE_CONTENT = "Storage file has invalid content";
    public static final String PERSON_NOT_IN_ADDRESSBOOK = "Person could not be found in address book";
    public static final String ERROR_CREATING_STORAGE_FILE = "Error: unable to create file: %1$s";
    public static final String ERROR_MISSING_STORAGE_FILE = "Storage file missing: %1$s";
    public static final String ERROR_READING_FROM_FILE = "Unexpected error: unable to read from file: %1$s";
    public static final String ERROR_WRITING_TO_FILE = "Unexpected error: unable to write to file: %1$s";
    public static final String PERSONS_FOUND_OVERVIEW = "%1$d persons found!";
    public static final String STORAGE_FILE_CREATED = "Created new empty storage file: %1$s";
    public static final String WELCOME = "Welcome to your Address Book!";
    public static final String USING_DEFAULT_FILE = "Using default storage file : " + AddressBook.DEFAULT_STORAGE_FILEPATH;
    
}

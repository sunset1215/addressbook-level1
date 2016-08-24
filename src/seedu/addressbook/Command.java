package seedu.addressbook;

/**
 * Stores the list of commands available and their respective description/usage in the address book.
 *
 * @author Vivian
 *
 */
public final class Command {
	
	private Command() {}

    public static final String ADD_WORD = "add";
    public static final String ADD_DESC = "Adds a person to the address book.";
    public static final String ADD_PARAMETERS = "NAME "
                                                      + Person.DATA_PREFIX_PHONE + "PHONE_NUMBER "
                                                      + Person.DATA_PREFIX_EMAIL + "EMAIL";
    public static final String ADD_EXAMPLE = ADD_WORD + " John Doe p/98765432 e/johnd@gmail.com";

    public static final String FIND_WORD = "find";
    public static final String FIND_DESC = "Finds all persons whose names contain any of the specified "
                                        + "keywords (case-sensitive) and displays them as a list with index numbers.";
    public static final String FIND_PARAMETERS = "KEYWORD [MORE_KEYWORDS]";
    public static final String FIND_EXAMPLE = FIND_WORD + " alice bob charlie";

    public static final String LIST_WORD = "list";
    public static final String LIST_DESC = "Displays all persons as a list with index numbers.";
    public static final String LIST_EXAMPLE = LIST_WORD;

    public static final String DELETE_WORD = "delete";
    public static final String DELETE_DESC = "Deletes a person identified by the index number used in "
                                                    + "the last find/list call.";
    public static final String DELETE_PARAMETER = "INDEX";
    public static final String DELETE_EXAMPLE = DELETE_WORD + " 1";

    public static final String CLEAR_WORD = "clear";
    public static final String CLEAR_DESC = "Clears address book permanently.";
    public static final String CLEAR_EXAMPLE = CLEAR_WORD;

    public static final String HELP_WORD = "help";
    public static final String HELP_DESC = "Shows program usage instructions.";
    public static final String HELP_EXAMPLE = HELP_WORD;

    public static final String EXIT_WORD = "exit";
    public static final String EXIT_DESC = "Exits the program.";
    public static final String EXIT_EXAMPLE = EXIT_WORD;
    
}

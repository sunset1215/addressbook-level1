package seedu.addressbook;
/* ==============NOTE TO STUDENTS======================================
 * This class is written in a procedural fashion (i.e. not Object-Oriented)
 * Yes, it is possible to write non-OO code using an OO language.
 * ====================================================================
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/* ==============NOTE TO STUDENTS======================================
 * This class header comment below is brief because details of how to
 * use this class are documented elsewhere.
 * ====================================================================
 */

/**
 * This class is used to maintain a list of person data which are saved
 * in a text file.
 **/
public class AddressBook {

    /**
     * Default file path used if the user doesn't provide the file name.
     */
    public static final String DEFAULT_STORAGE_FILEPATH = "addressbook.txt";

    /**
     * Version info of the program.
     */
    private static final String VERSION = "AddessBook Level 1 - Version 1.0";

    /**
     * A decorative prefix added to the beginning of lines printed by AddressBook
     */
    public static final String LINE_PREFIX = "|| ";

    /**
     * A platform independent line separator.
     */
    public static final String LS = System.lineSeparator() + LINE_PREFIX;

    private static final String DIVIDER = "===================================================";

    /**
     * Offset required to convert between 1-indexing and 0-indexing.COMMAND_
     */
    private static final int DISPLAYED_INDEX_OFFSET = 1;

    /**
     * If the first non-whitespace character in a user's input line is this, that line will be ignored.
     */
    private static final char INPUT_COMMENT_MARKER = '#';

    /*
     * This variable is declared for the whole class (instead of declaring it
     * inside the readUserCommand() method to facilitate automated testing using
     * the I/O redirection technique. If not, only the first line of the input
     * text file will be processed.
     */
    private static final Scanner SCANNER = new Scanner(System.in);
    /*
     * ==============NOTE TO STUDENTS======================================================================
     * Note that the type of the variable below can also be declared as List<String[]>, as follows:
     *    private static final List<String[]> ALL_PERSONS = new ArrayList<>()
     * That is because List is an interface implemented by the ArrayList class.
     * In this code we use ArrayList instead because we wanted to to stay away from advanced concepts
     * such as interface inheritance.
     * ====================================================================================================
     */
    /**
     * List of all persons in the address book.
     */
    private static final ArrayList<Person> ALL_PERSONS = new ArrayList<>();


    /**
     * Stores the most recent list of persons shown to the user as a result of a user command.
     * This is a subset of the full list. Deleting persons in the pull list does not delete
     * those persons from this list.
     */
    private static ArrayList<Person> latestPersonListingView = getAllPersonsInAddressBook(); // initial view is of all

    /**
     * The path to the file used for storing person data.
     */
    private static String storageFilePath;

    /*
     * ==============NOTE TO STUDENTS======================================
     * Notice how this method solves the whole problem at a very high level.
     * We can understand the high-level logic of the program by reading this
     * method alone.
     * ====================================================================
     */
    public static void main(String[] args) {
        showWelcomeMessage();
        processProgramArgs(args);
        loadDataFromStorage();
        while (true) {
            String userCommand = getUserInput();
            echoUserCommand(userCommand);
            String feedback = executeCommand(userCommand);
            showResultToUser(feedback);
        }
    }

    /*
     * ==============NOTE TO STUDENTS======================================
     * The method header comment can be omitted if the method is trivial
     * and the header comment is going to be almost identical to the method
     * signature anyway.
     * ====================================================================
     */
    private static void showWelcomeMessage() {
        showToUser(DIVIDER, DIVIDER, VERSION, Message.WELCOME, DIVIDER);
    }

    private static void showResultToUser(String result) {
        showToUser(result, DIVIDER);
    }

    /*
     * ==============NOTE TO STUDENTS======================================
     * Parameter description can be omitted from the method header comment
     * if the parameter name is self-explanatory.
     * In the method below, '@param userInput' comment has been omitted.
     * ====================================================================
     */
    /**
     * Echoes the user input back to the user.
     */
    private static void echoUserCommand(String userCommand) {
        showToUser("[Command entered:" + userCommand + "]");
    }

    /*
     * ==============NOTE TO STUDENTS==========================================
     * If the reader wants a deeper understanding of the solution, she can go
     * to the next level of abstraction by reading the methods (given below)
     * that is referenced by the method above.
     * ====================================================================
     */

    /**
     * Processes the program main method run arguments.
     * If a valid storage file is specified, sets up that file for storage.
     * Otherwise sets up the default file for storage.
     *
     * @param args full program arguments passed to application main method
     */
    private static void processProgramArgs(String[] args) {
        if (args.length >= 2) {
            showToUser(Message.INVALID_PROGRAM_ARGS);
            exitProgram();
        }

        if (args.length == 1) {
            setupGivenFileForStorage(args[0]);
        }

        if(args.length == 0) {
            setupDefaultFileForStorage();
        }
    }

    /**
     * Sets up the storage file based on the supplied file path.
     * Creates the file if it is missing.
     * Exits if the file name is not acceptable.
     */
    private static void setupGivenFileForStorage(String filePath) {

        if (!isValidFilePath(filePath)) {
            showToUser(String.format(Message.INVALID_FILE, filePath));
            exitProgram();
        }

        storageFilePath = filePath;
        createFileIfMissing(filePath);
    }

    /**
     * Displays the goodbye message and exits the runtime.
     */
    private static void exitProgram() {
        showToUser(Message.GOODBYE, DIVIDER, DIVIDER);
        System.exit(0);
    }

    /**
     * Sets up the storage based on the default file.
     * Creates file if missing.
     * Exits program if the file cannot be created.
     */
    private static void setupDefaultFileForStorage() {
        showToUser(Message.USING_DEFAULT_FILE);
        storageFilePath = DEFAULT_STORAGE_FILEPATH;
        createFileIfMissing(storageFilePath);
    }

    /**
     * Returns true if the given file is acceptable.
     * The file path is acceptable if it ends in '.txt'
     * TODO: Implement a more rigorous validity checking.
     */
    private static boolean isValidFilePath(String filePath) {
        return filePath.endsWith(".txt");
    }

    /**
     * Initialises the in-memory data using the storage file.
     * Assumption: The file exists.
     */
    private static void loadDataFromStorage() {
        initialiseAddressBookModel(loadPersonsFromFile(storageFilePath));
    }


    /*
     * ===========================================
     *           COMMAND LOGIC
     * ===========================================
     */

    /**
     * Executes the command as specified by the {@code userInputString}
     *
     * @param userInputString  raw input from user
     * @return  feedback about how the command was executed
     */
    public static String executeCommand(String userInputString) {
        final String[] commandTypeAndParams = splitCommandWordAndArgs(userInputString);
        final String commandType = commandTypeAndParams[0];
        final String commandArgs = commandTypeAndParams[1];
        switch (commandType) {
        case Command.ADD_WORD:
            return executeAddPerson(commandArgs);
        case Command.FIND_WORD:
            return executeFindPersons(commandArgs);
        case Command.LIST_WORD:
            return executeListAllPersonsInAddressBook();
        case Command.DELETE_WORD:
            return executeDeletePerson(commandArgs);
        case Command.CLEAR_WORD:
            return executeClearAddressBook();
        case Command.HELP_WORD:
            return getUsageInfoForAllCommands();
        case Command.EXIT_WORD:
            executeExitProgramRequest();
        default:
            return getMessageForInvalidCommandInput(commandType, getUsageInfoForAllCommands());
        }
    }

    /**
     * Splits raw user input into command word and command arguments string
     *
     * @return  size 2 array; first element is the command type and second element is the arguments string
     */
    private static String[] splitCommandWordAndArgs(String rawUserInput) {
        final String[] split =  rawUserInput.trim().split("\\s+", 2);
        return split.length == 2 ? split : new String[] { split[0] , "" }; // else case: no parameters
    }

    /**
     * Constructs a generic feedback message for an invalid command from user, with instructions for correct usage.
     *
     * @param correctUsageInfo message showing the correct usage
     * @return invalid command args feedback message
     */
    private static String getMessageForInvalidCommandInput(String userCommand, String correctUsageInfo) {
        return String.format(Message.INVALID_COMMAND_FORMAT, userCommand, correctUsageInfo);
    }

    /**
     * Adds a person (specified by the command args) to the address book.
     * The entire command arguments string is treated as a string representation of the person to add.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeAddPerson(String commandArgs) {
        // try decoding a person from the raw args
        final Optional<Person> decodeResult = Person.decodePersonFromString(commandArgs);

        // checks if args are valid (decode result will not be present if the person is invalid)
        if (!decodeResult.isPresent()) {
            return getMessageForInvalidCommandInput(Command.ADD_WORD, getUsageInfoForAddCommand());
        }

        // add the person as specified
        final Person personToAdd = decodeResult.get();
        addPersonToAddressBook(personToAdd);
        return getMessageForSuccessfulAddPerson(personToAdd);
    }

    /**
     * Constructs a feedback message for a successful add person command execution.
     *
     * @see #executeAddPerson(String)
     * @param addedPerson person who was successfully added
     * @return successful add person feedback message
     */
    private static String getMessageForSuccessfulAddPerson(Person addedPerson) {
        return String.format(Message.ADDED,
                addedPerson.getName(), addedPerson.getPhone(), addedPerson.getEmail());
    }

    /**
     * Finds and lists all persons in address book whose name contains any of the argument keywords.
     * Keyword matching is case sensitive.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeFindPersons(String commandArgs) {
        final Set<String> keywords = extractKeywordsFromFindPersonArgs(commandArgs);
        final ArrayList<Person> personsFound = getPersonsWithNameContainingAnyKeyword(keywords);
        showToUser(personsFound);
        return getMessageForPersonsDisplayedSummary(personsFound);
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of persons.
     *
     * @param personsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    private static String getMessageForPersonsDisplayedSummary(ArrayList<Person> personsDisplayed) {
        return String.format(Message.PERSONS_FOUND_OVERVIEW, personsDisplayed.size());
    }

    /**
     * Extract keywords from the command arguments given for the find persons command.
     *
     * @param findPersonCommandArgs full command args string for the find persons command
     * @return set of keywords as specified by args
     */
    private static Set<String> extractKeywordsFromFindPersonArgs(String findPersonCommandArgs) {
        return new HashSet<>(Utility.splitByWhitespace(findPersonCommandArgs.trim()));
    }

    /**
     * Retrieve all persons in the full model whose names contain some of the specified keywords.
     *
     * @param keywords for searching
     * @return list of persons in full model with name containing some of the keywords
     */
    private static ArrayList<Person> getPersonsWithNameContainingAnyKeyword(Collection<String> keywords) {
        final ArrayList<Person> matchedPersons = new ArrayList<>();
        for (Person person : getAllPersonsInAddressBook()) {
            final Set<String> wordsInName = new HashSet<>(Utility.splitByWhitespace(person.getName()));
            if (!Collections.disjoint(wordsInName, keywords)) {
                matchedPersons.add(person);
            }
        }
        return matchedPersons;
    }

    /**
     * Deletes person identified using last displayed index.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeDeletePerson(String commandArgs) {
        if (!isDeletePersonArgsValid(commandArgs)) {
            return getMessageForInvalidCommandInput(Command.DELETE_WORD, getUsageInfoForDeleteCommand());
        }
        final int targetVisibleIndex = extractTargetIndexFromDeletePersonArgs(commandArgs);
        if (!isDisplayIndexValidForLastPersonListingView(targetVisibleIndex)) {
            return Message.INVALID_PERSON_DISPLAYED_INDEX;
        }
        final Person targetInModel = getPersonByLastVisibleIndex(targetVisibleIndex);
        return deletePersonFromAddressBook(targetInModel) ? getMessageForSuccessfulDelete(targetInModel) // success
                                                          : Message.PERSON_NOT_IN_ADDRESSBOOK; // not found
    }

    /**
     * Checks validity of delete person argument string's format.
     *
     * @param rawArgs raw command args string for the delete person command
     * @return whether the input args string is valid
     */
    private static boolean isDeletePersonArgsValid(String rawArgs) {
        try {
            final int extractedIndex = Integer.parseInt(rawArgs.trim()); // use standard libraries to parse
            return extractedIndex >= DISPLAYED_INDEX_OFFSET;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Extracts the target's index from the raw delete person args string
     *
     * @param rawArgs raw command args string for the delete person command
     * @return extracted index
     */
    private static int extractTargetIndexFromDeletePersonArgs(String rawArgs) {
        return Integer.parseInt(rawArgs.trim());
    }

    /**
     * Checks that the given index is within bounds and valid for the last shown person list view.
     *
     * @param index to check
     * @return whether it is valid
     */
    private static boolean isDisplayIndexValidForLastPersonListingView(int index) {
        return index >= DISPLAYED_INDEX_OFFSET && index < getLatestPersonListingView().size() + DISPLAYED_INDEX_OFFSET;
    }

    /**
     * Constructs a feedback message for a successful delete person command execution.
     *
     * @see #executeDeletePerson(String)
     * @param deletedPerson successfully deleted
     * @return successful delete person feedback message
     */
    private static String getMessageForSuccessfulDelete(Person deletedPerson) {
        return String.format(Message.DELETE_PERSON_SUCCESS, getMessageForFormattedPersonData(deletedPerson));
    }

    /**
     * Clears all persons in the address book.
     *
     * @return feedback display message for the operation result
     */
    private static String executeClearAddressBook() {
        clearAddressBook();
        return Message.ADDRESSBOOK_CLEARED;
    }

    /**
     * Displays all persons in the address book to the user; in added order.
     *
     * @return feedback display message for the operation result
     */
    private static String executeListAllPersonsInAddressBook() {
        ArrayList<Person> toBeDisplayed = getAllPersonsInAddressBook();
        showToUser(toBeDisplayed);
        return getMessageForPersonsDisplayedSummary(toBeDisplayed);
    }

    /**
     * Request to terminate the program.
     *
     * @return feedback display message for the operation result
     */
    private static void executeExitProgramRequest() {
        exitProgram();
    }

    /*
     * ===========================================
     *               UI LOGIC
     * ===========================================
     */

    /**
     * Prompts for the command and reads the text entered by the user.
     * Ignores lines with first non-whitespace char equal to {@link #INPUT_COMMENT_MARKER} (considered comments)
     *
     * @return full line entered by the user
     */
    private static String getUserInput() {
        System.out.print(LINE_PREFIX + "Enter command: ");
        String inputLine = SCANNER.nextLine();
        // silently consume all blank and comment lines
        while (inputLine.trim().isEmpty() || inputLine.trim().charAt(0) == INPUT_COMMENT_MARKER) {
            inputLine = SCANNER.nextLine();
        }
        return inputLine;
    }

   /* ==============NOTE TO STUDENTS======================================
    * Note how the method below uses Java 'Varargs' feature so that the
    * method can accept a varying number of message parameters.
    * ====================================================================
    */
    /**
     * Shows a message to the user
     */
    private static void showToUser(String... message) {
        for (String m : message) {
            System.out.println(LINE_PREFIX + m);
        }
    }

    /**
     * Shows the list of persons to the user.
     * The list will be indexed, starting from 1.
     *
     */
    private static void showToUser(ArrayList<Person> persons) {
        String listAsString = getDisplayString(persons);
        showToUser(listAsString);
        updateLatestViewedPersonListing(persons);
    }

    /**
     * Returns the display string representation of the list of persons.
     */
    private static String getDisplayString(ArrayList<Person> persons) {
        final StringBuilder messageAccumulator = new StringBuilder();
        for (int i = 0; i < persons.size(); i++) {
            final Person person = persons.get(i);
            final int displayIndex = i + DISPLAYED_INDEX_OFFSET;
            messageAccumulator.append('\t')
                              .append(getIndexedPersonListElementMessage(displayIndex, person))
                              .append(LS);
        }
        return messageAccumulator.toString();
    }

    /**
     * Constructs a prettified listing element message to represent a person and their data.
     *
     * @param visibleIndex visible index for this listing
     * @param person to show
     * @return formatted listing message with index
     */
    private static String getIndexedPersonListElementMessage(int visibleIndex, Person person) {
        return String.format(Message.DISPLAY_LIST_ELEMENT_INDEX, visibleIndex) + getMessageForFormattedPersonData(person);
    }

    /**
     * Constructs a prettified string to show the user a person's data.
     *
     * @param person to show
     * @return formatted message showing internal state
     */
    private static String getMessageForFormattedPersonData(Person person) {
        return String.format(Message.DISPLAY_PERSON_DATA,
                person.getName(), person.getPhone(), person.getEmail());
    }

    /**
     * Updates the latest person listing view the user has seen.
     *
     * @param newListing the new listing of persons
     */
    private static void updateLatestViewedPersonListing(ArrayList<Person> newListing) {
        // clone to insulate from future changes to arg list
        latestPersonListingView = new ArrayList<>(newListing);
    }

    /**
     * Retrieves the person identified by the displayed index from the last shown listing of persons.
     *
     * @param lastVisibleIndex displayed index from last shown person listing
     * @return the actual person object in the last shown person listing
     */
    private static Person getPersonByLastVisibleIndex(int lastVisibleIndex) {
       return latestPersonListingView.get(lastVisibleIndex - DISPLAYED_INDEX_OFFSET);
    }

    /**
     * @return unmodifiable list view of the last person listing view
     */
    private static ArrayList<Person> getLatestPersonListingView() {
        return latestPersonListingView;
    }


    /*
     * ===========================================
     *             STORAGE LOGIC
     * ===========================================
     */

    /**
     * Creates storage file if it does not exist. Shows feedback to user.
     *
     * @param filePath file to create if not present
     */
    private static void createFileIfMissing(String filePath) {
        final File storageFile = new File(filePath);
        if (storageFile.exists()) {
            return;
        }

        showToUser(String.format(Message.ERROR_MISSING_STORAGE_FILE, filePath));

        try {
            storageFile.createNewFile();
            showToUser(String.format(Message.STORAGE_FILE_CREATED, filePath));
        } catch (IOException ioe) {
            showToUser(String.format(Message.ERROR_CREATING_STORAGE_FILE, filePath));
            exitProgram();
        }
    }

    /**
     * Converts contents of a file into a list of persons.
     * Shows error messages and exits program if any errors in reading or decoding was encountered.
     *
     * @param filePath file to load from
     * @return the list of decoded persons
     */
    private static ArrayList<Person> loadPersonsFromFile(String filePath) {
        final Optional<ArrayList<Person>> successfullyDecoded = Person.decodePersonsFromStrings(getLinesInFile(filePath));
        if (!successfullyDecoded.isPresent()) {
            showToUser(Message.INVALID_STORAGE_FILE_CONTENT);
            exitProgram();
        }
        return successfullyDecoded.get();
    }

    /**
     * Gets all lines in the specified file as a list of strings. Line separators are removed.
     * Shows error messages and exits program if unable to read from file.
     */
    private static ArrayList<String> getLinesInFile(String filePath) {
        ArrayList<String> lines = null;
        try {
            lines = new ArrayList(Files.readAllLines(Paths.get(filePath)));
        } catch (FileNotFoundException fnfe) {
            showToUser(String.format(Message.ERROR_MISSING_STORAGE_FILE, filePath));
            exitProgram();
        } catch (IOException ioe) {
            showToUser(String.format(Message.ERROR_READING_FROM_FILE, filePath));
            exitProgram();
        }
        return lines;
    }

    /**
     * Saves all data to the file.
     * Exits program if there is an error saving to file.
     *
     * @param filePath file for saving
     */
    private static void savePersonsToFile(ArrayList<Person> persons, String filePath) {
        final ArrayList<String> linesToWrite = Person.encodePersonsToStrings(persons);
        try {
            Files.write(Paths.get(storageFilePath), linesToWrite);
        } catch (IOException ioe) {
            showToUser(String.format(Message.ERROR_WRITING_TO_FILE, filePath));
            exitProgram();
        }
    }


    /*
     * ================================================================================
     *        INTERNAL ADDRESS BOOK DATA METHODS
     * ================================================================================
     */

    /**
     * Adds a person to the address book. Saves changes to storage file.
     *
     * @param person to add
     */
    private static void addPersonToAddressBook(Person person) {
        ALL_PERSONS.add(person);
        savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
    }

    /**
     * Deletes a person from the address book, target is identified by it's absolute index in the full list.
     * Saves changes to storage file.
     *
     * @param index absolute index of person to delete (index within {@link #ALL_PERSONS})
     */
    private static void deletePersonFromAddressBook(int index) {
        ALL_PERSONS.remove(index);
        savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
    }

    /**
     * Deletes the specified person from the addressbook if it is inside. Saves any changes to storage file.
     *
     * @param exactPerson the actual person inside the address book (exactPerson == the person to delete in the full list)
     * @return true if the given person was found and deleted in the model
     */
    private static boolean deletePersonFromAddressBook(Person exactPerson) {
        final boolean changed = ALL_PERSONS.remove(exactPerson);
        if (changed) {
            savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
        }
        return changed;
    }

    /**
     * @return unmodifiable list view of all persons in the address book
     */
    private static ArrayList<Person> getAllPersonsInAddressBook() {
        return ALL_PERSONS;
    }

    /**
     * Clears all persons in the address book and saves changes to file.
     */
    private static void clearAddressBook() {
        ALL_PERSONS.clear();
        savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
    }

    /**
     * Resets the internal model with the given data. Does not save to file.
     *
     * @param persons list of persons to initialise the model with
     */
    private static void initialiseAddressBookModel(ArrayList<Person> persons) {
        ALL_PERSONS.clear();
        ALL_PERSONS.addAll(persons);
    }

    
    /*
     * ===============================================
     *         COMMAND HELP INFO FOR USERS
     * ===============================================
     */

    /**
     * @return  Usage info for all commands
     */
    private static String getUsageInfoForAllCommands() {
        return getUsageInfoForAddCommand() + LS
                + getUsageInfoForFindCommand() + LS
                + getUsageInfoForViewCommand() + LS
                + getUsageInfoForDeleteCommand() + LS
                + getUsageInfoForClearCommand() + LS
                + getUsageInfoForExitCommand() + LS
                + getUsageInfoForHelpCommand();
    }

    /**
     * Builds string for showing 'add' command usage instruction
     *
     * @return  'add' command usage instruction
     */
    private static String getUsageInfoForAddCommand() {
        return String.format(Message.COMMAND_HELP, Command.ADD_WORD, Command.ADD_DESC) + LS
                + String.format(Message.COMMAND_HELP_PARAMETERS, Command.ADD_PARAMETERS) + LS
                + String.format(Message.COMMAND_HELP_EXAMPLE,Command.ADD_EXAMPLE) + LS;
    }

    /**
     * Builds string for showing 'find' command usage instruction
     *
     * @return  'find' command usage instruction
     */
    private static String getUsageInfoForFindCommand() {
        return String.format(Message.COMMAND_HELP,Command.FIND_WORD, Command.FIND_DESC) + LS
                + String.format(Message.COMMAND_HELP_PARAMETERS, Command.FIND_PARAMETERS) + LS
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.FIND_EXAMPLE) + LS;
    }

    /**
     * Builds string for showing 'delete' command usage instruction
     *
     * @return  'delete' command usage instruction
     */
    private static String getUsageInfoForDeleteCommand() {
        return String.format(Message.COMMAND_HELP, Command.DELETE_WORD, Command.DELETE_DESC) + LS
                + String.format(Message.COMMAND_HELP_PARAMETERS, Command.DELETE_PARAMETER) + LS
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.DELETE_EXAMPLE) + LS;
    }

    /**
     * Builds string for showing 'clear' command usage instruction
     *
     * @return  'clear' command usage instruction
     */
    private static String getUsageInfoForClearCommand() {
        return String.format(Message.COMMAND_HELP, Command.CLEAR_WORD, Command.CLEAR_DESC) + LS
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.CLEAR_EXAMPLE) + LS;
    }

    /**
     * Builds string for showing 'view' command usage instruction
     *
     * @return  'view' command usage instruction
     */
    private static String getUsageInfoForViewCommand() {
        return String.format(Message.COMMAND_HELP, Command.LIST_WORD, Command.LIST_DESC) + LS
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.LIST_EXAMPLE) + LS;
    }

    /**
     * Builds string for showing 'help' command usage instruction
     *
     * @return  'help' command usage instruction
     */
    private static String getUsageInfoForHelpCommand() {
        return String.format(Message.COMMAND_HELP, Command.HELP_WORD, Command.HELP_DESC)
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.HELP_EXAMPLE);
    }

    /**
     * Builds string for showing 'exit' command usage instruction
     *
     * @return  'exit' command usage instruction
     */
    private static String getUsageInfoForExitCommand() {
        return String.format(Message.COMMAND_HELP, Command.EXIT_WORD, Command.EXIT_DESC)
                + String.format(Message.COMMAND_HELP_EXAMPLE, Command.EXIT_EXAMPLE);
    }


}
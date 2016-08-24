package seedu.addressbook;

import java.util.ArrayList;
import java.util.Optional;

public class Person {

    // These are the prefix strings to define the data type of a command parameter
    public static final String DATA_PREFIX_PHONE = "p/";
    public static final String DATA_PREFIX_EMAIL = "e/";

    private static final String STRING_REPRESENTATION = "%1$s " // name
                                                     + DATA_PREFIX_PHONE + "%2$s " // phone
                                                     + DATA_PREFIX_EMAIL + "%3$s"; // email
    
    private String _name;
    private String _email;
    private String _phone;
    
    public Person() {}
    
    public Person(String name, String phone, String email) {
    	_name = name;
    	_email = email;
    	_phone = phone;
    }
    
    public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String email) {
		_email = email;
	}

	public String getPhone() {
		return _phone;
	}

	public void setPhone(String phone) {
		_phone = phone;
	}


    /**
     * Encodes a person into a decodable and readable string representation.
     *
     * @param person to be encoded
     * @return encoded string
     */
    private static String encodePersonToString(Person person) {
        return String.format(STRING_REPRESENTATION,
                person.getName(), person.getPhone(), person.getEmail());
    }

    /**
     * Encodes list of persons into list of decodable and readable string representations.
     *
     * @param persons to be encoded
     * @return encoded strings
     */
    public static ArrayList<String> encodePersonsToStrings(ArrayList<Person> persons) {
        final ArrayList<String> encoded = new ArrayList<>();
        for (Person person : persons) {
            encoded.add(encodePersonToString(person));
        }
        return encoded;
    }

    /*
     * ==============NOTE TO STUDENTS======================================
     * Note the use of Java's new 'Optional' feature to indicate that
     * the return value may not always be present.
     * ====================================================================
     */
    /**
     * Decodes a person from it's supposed string representation.
     *
     * @param encoded string to be decoded
     * @return if cannot decode: empty Optional
     *         else: Optional containing decoded person
     */
    public static Optional<Person> decodePersonFromString(String encoded) {
        // check that we can extract the parts of a person from the encoded string
        if (!isPersonDataExtractableFrom(encoded)) {
            return Optional.empty();
        }
        final Person decodedPerson = new Person(
                extractNameFromPersonString(encoded),
                extractPhoneFromPersonString(encoded),
                extractEmailFromPersonString(encoded)
        );
        // check that the constructed person is valid
        return isPersonDataValid(decodedPerson) ? Optional.of(decodedPerson) : Optional.empty();
    }

    /**
     * Decode persons from a list of string representations.
     *
     * @param encodedPersons strings to be decoded
     * @return if cannot decode any: empty Optional
     *         else: Optional containing decoded persons
     */
    public static Optional<ArrayList<Person>> decodePersonsFromStrings(ArrayList<String> encodedPersons) {
        final ArrayList<Person> decodedPersons = new ArrayList<>();
        for (String encodedPerson : encodedPersons) {
            final Optional<Person> decodedPerson = decodePersonFromString(encodedPerson);
            if (!decodedPerson.isPresent()) {
                return Optional.empty();
            }
            decodedPersons.add(decodedPerson.get());
        }
        return Optional.of(decodedPersons);
    }

    /**
     * Checks whether person data (email, name, phone etc) can be extracted from the argument string.
     * Format is [name] p/[phone] e/[email], phone and email positions can be swapped.
     *
     * @param personData person string representation
     * @return whether format of add command arguments allows parsing into individual arguments
     */
    private static boolean isPersonDataExtractableFrom(String personData) {
        final String matchAnyPersonDataPrefix = DATA_PREFIX_PHONE + '|' + DATA_PREFIX_EMAIL;
        final String[] splitArgs = personData.trim().split(matchAnyPersonDataPrefix);
        return splitArgs.length == 3 // 3 arguments
                && !splitArgs[0].isEmpty() // non-empty arguments
                && !splitArgs[1].isEmpty()
                && !splitArgs[2].isEmpty();
    }

    /**
     * Extracts substring representing person name from person string representation
     *
     * @param encoded person string representation
     * @return name argument
     */
    private static String extractNameFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(DATA_PREFIX_EMAIL);
        // name is leading substring up to first data prefix symbol
        int indexOfFirstPrefix = Math.min(indexOfEmailPrefix, indexOfPhonePrefix);
        return encoded.substring(0, indexOfFirstPrefix).trim();
    }

    /**
     * Extracts substring representing phone number from person string representation
     *
     * @param encoded person string representation
     * @return phone number argument WITHOUT prefix
     */
    private static String extractPhoneFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(DATA_PREFIX_EMAIL);

        // phone is last arg, target is from prefix to end of string
        if (indexOfPhonePrefix > indexOfEmailPrefix) {
            return Utility.removePrefixSign(encoded.substring(indexOfPhonePrefix, encoded.length()).trim(),
                    DATA_PREFIX_PHONE);

        // phone is middle arg, target is from own prefix to next prefix
        } else {
            return Utility.removePrefixSign(
                    encoded.substring(indexOfPhonePrefix, indexOfEmailPrefix).trim(),
                    DATA_PREFIX_PHONE);
        }
    }

    /**
     * Extracts substring representing email from person string representation
     *
     * @param encoded person string representation
     * @return email argument WITHOUT prefix
     */
    private static String extractEmailFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(DATA_PREFIX_EMAIL);

        // email is last arg, target is from prefix to end of string
        if (indexOfEmailPrefix > indexOfPhonePrefix) {
            return Utility.removePrefixSign(encoded.substring(indexOfEmailPrefix, encoded.length()).trim(),
                    DATA_PREFIX_EMAIL);

        // email is middle arg, target is from own prefix to next prefix
        } else {
            return Utility.removePrefixSign(
                    encoded.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim(),
                    DATA_PREFIX_EMAIL);
        }
    }

    /**
     * Validates a person's data fields
     *
     * @param person String array representing the person (used in internal data)
     * @return whether the given person has valid data
     */
    private static boolean isPersonDataValid(Person person) {
        return isPersonNameValid(person.getName())
                && isPersonPhoneValid(person.getPhone())
                && isPersonEmailValid(person.getEmail());
    }

    /*
     * ==============NOTE TO STUDENTS======================================
     * Note the use of 'regular expressions' in the method below.
     * Regular expressions can be very useful in checking if a a string
     * follows a sepcific format.
     * ====================================================================
     */
    /**
     * Validates string as a legal person name
     *
     * @param name to be validated
     * @return whether arg is a valid person name
     */
    private static boolean isPersonNameValid(String name) {
        return name.matches("(\\w|\\s)+");  // name is nonempty mixture of alphabets and whitespace
        //TODO: implement a more permissive validation
    }

    /**
     * Validates string as a legal person phone number
     *
     * @param phone to be validated
     * @return whether arg is a valid person phone number
     */
    private static boolean isPersonPhoneValid(String phone) {
        return phone.matches("\\d+");    // phone nonempty sequence of digits
        //TODO: implement a more permissive validation
    }

    /**
     * Validates string as a legal person email
     *
     * @param email to be validated
     * @return whether arg is a valid person email
     */
    private static boolean isPersonEmailValid(String email) {
        return email.matches("\\S+@\\S+\\.\\S+"); // email is [non-whitespace]@[non-whitespace].[non-whitespace]
        //TODO: implement a more permissive validation
    }


}

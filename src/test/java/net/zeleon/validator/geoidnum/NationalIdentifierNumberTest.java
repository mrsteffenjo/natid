package net.zeleon.validator.geoidnum;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by zeleo on 15.10.2015.
 */
public class NationalIdentifierNumberTest {

    @Test
    public void can_understand_valid_numbers() {
        for (String number : TestDataGenerator.validNumbers()) {
            assertNotNull(NationalIdentificationNumber.from(number));
        }

    }

    @Test
    public void can_understand_invalid_numbers_and_raises_error_message() {
        for (String number : TestDataGenerator.invalidNumbers()) {
            try {
                assertNull(NationalIdentificationNumber.from(number));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                assertNotNull(e.getMessage());
            }
        }

    }

    @Test
    public void can_get_date_of_birth_for_valid_numbers() {
        for (String number : TestDataGenerator.validNumbers()) {
            try {
                LocalDate birthDate = NationalIdentificationNumber.from(number).getBirthDate();
                assertNotNull(birthDate);
                System.out.println(String.format("%s, birth date was resolved from \"%s\"", birthDate.toString(), number));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                assertTrue(false);
            }
        }

    }

    @Test
    public void can_find_sex_for_valid_male_numbers() {
        for (String number : TestDataGenerator.validMaleNumbers()) {
            try {
                NationalIdentificationNumber id = NationalIdentificationNumber.from(number);
                System.out.println(id);
                LocalDate birthDate = id.getBirthDate();
                assertTrue("M".equals(id.getGender()));
                assertNotNull(birthDate);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                assertTrue(false);
            }
        }

    }

    @Test
    public void can_find_sex_for_valid_female_numbers() {
        for (String number : TestDataGenerator.validFemaleNumbers()) {
            try {
                NationalIdentificationNumber id = NationalIdentificationNumber.from(number);
                System.out.println(id);
                LocalDate birthDate = id.getBirthDate();
                assertTrue("F".equals(id.getGender()));
                assertNotNull(birthDate);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                assertTrue(false);
            }
        }

    }
}

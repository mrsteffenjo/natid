package net.zeleon.validator.geoidnum;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Created by Steffen Jørgensen on 14.10.2015.
 */
public class NationalIdentificationNumber {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final int SIZE = 11;
    private final String day;
    private final String month;
    private final String year;
    private final String individualId;
    private final String checkDigits;

    private NationalIdentificationNumber(String day, String month, String year, String individualId, String checkDigits) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.individualId = individualId;
        this.checkDigits = checkDigits;
    }

    public String getDay() {
        return day;
    }

    public LocalDate getBirthDate() {
        return LocalDate.parse(getDay() + getMonth() + detectYear(getYear(), getIndividualId()), DATE_TIME_FORMAT);
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getIndividualId() {
        return individualId;
    }

    public String getCheckDigits() {
        return checkDigits;
    }

    public Integer getCheckDigitOne() {
        return Character.getNumericValue(getCheckDigits().charAt(0));
    }

    public Integer getCheckDigitTwo() {
        return Character.getNumericValue(getCheckDigits().charAt(1));
    }

    public String getGender() {
        return isParity((int) getIndividualId().charAt(getIndividualId().length() - 1)) ? "F" : "M";
    }

    public static NationalIdentificationNumber from(String fn) {
        Preconditions.checkArgument(isSyntaxValid(fn), String.format("Failed syntax for \"%s\"", fn));
        String prepared = keepDigits(fn);
        Preconditions.checkArgument(prepared.length() == SIZE, String.format("Number was to short \"%s\"", fn));
        Preconditions.checkArgument(modulusCheck(prepared), String.format("Failed modulus check for \"%s\"", fn));
        return new NationalIdentificationNumber(
                prepared.substring(0, 2),
                prepared.substring(2, 4),
                prepared.substring(4, 6),
                prepared.substring(6, 9),
                prepared.substring(9));
    }

    private static boolean modulusCheck(String prepared) {
        List<Character> chars = Lists.charactersOf(prepared);
        int[] ints = chars.stream().mapToInt(Character::getNumericValue).toArray();

        int checkSumPart1 = calculateCheckSumPart(getFirstCheckMultipliers(), ints);
        int checkSum1 = calculateCheckSum(checkSumPart1);

        int checkSumPart2 = calculateCheckSumPart(getSecondCheckMultipliers(), ints);
        int checkSum2 = calculateCheckSum(checkSumPart2);

        return !(checkSum1 == -1 || checkSum2 == -1) && checkSum1 == ints[9] && checkSum2 == ints[10];
    }

    private static int calculateCheckSumPart(int[] multipliers, int[] ints) {
        int checkSum = 0;
        for (int i = 0; i < multipliers.length; i++) {
            checkSum+=multipliers[i]*ints[i];
        }
        return checkSum;
    }

    private static int calculateCheckSum(int checkSumPart) {
        int checkSum = 11 - (checkSumPart % 11);
        if (checkSum == 10) return -1;
        checkSum = checkSum == 11 ? 0 : checkSum;
        return checkSum;
    }

    private static int[] getFirstCheckMultipliers(){
        return new int[] {3,7,6,1,8,9,4,5,2};
    }

    private static int[] getSecondCheckMultipliers(){
        return new int[] {5,4,3,2,7,6,5,4,3,2};
    }

    private boolean isParity(int value) {
        return value % 2 == 0;
    }

    private static boolean isSyntaxValid(String fn) {
        return !Strings.isNullOrEmpty(fn) && fn.trim().matches("[0-9]{2}[0,1][0-9][0-9]{2}[ ]?[0-9]{5}") && fn.length() >= SIZE;
    }

    private static String keepDigits(String number) {
        Objects.requireNonNull(number);
        return Lists.charactersOf(number).stream().filter(Character::isDigit).collect(StringBuilder::new, StringBuilder::append,
                StringBuilder::append)
                .toString();
    }

    private String detectYear(String year, String individualId) {
        int individualNumber = Integer.parseInt(individualId);
        for (Integer possibleYear : getPossibleYears(year)) {
            if (checkYearToIndividualNumber(possibleYear, individualNumber))
                return possibleYear.toString();
        }
        return String.valueOf(LocalDate.now().getYear());
    }

    private boolean checkYearToIndividualNumber(Integer year, int individualNumber) {
        return isInRange(1900, 1999, year) && isInRange(0, 499, individualNumber) ||
                isInRange(1854, 1899, year) && isInRange(500, 749, individualNumber) ||
                isInRange(2000, 2039, year) && isInRange(500, 999, individualNumber) ||
                isInRange(1940, 1999, year) && isInRange(900, 999, individualNumber);
    }

    private List<Integer> getPossibleYears(String year) {
        List<Integer> possibleYears = Lists.newArrayList();
        possibleYears.add(Integer.valueOf("19" + year));
        possibleYears.add(Integer.valueOf("18" + year));
        possibleYears.add(Integer.valueOf("20" + year));
        return possibleYears;
    }

    private boolean isInRange(int min, int max, int number) {
        return (number >= min && number <= max);
    }

    @Override
    public String toString() {
        return day + month + year +" "+individualId+checkDigits+": "+getBirthDate().toString() + ", " + getGender() + ", " + getCheckDigitOne() + ", " + getCheckDigitTwo() ;
    }
}

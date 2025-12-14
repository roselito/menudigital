package com.rfs.menudigital.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 *
 * @author Roselito@RFS
 */
@Component
public class NumberConverter {
    public String ptBrEnUs(String ptBrNumberString){
        String enUsNumberString = "";
        try {
            // 1. Parse the pt-BR string into a neutral Java Number object (like a Double or Long)
            // Use the Brazilian locale for parsing (comma is decimal separator, dot is group separator)
            NumberFormat inputFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
            Number number = inputFormat.parse(ptBrNumberString);

            // 2. Format the neutral Number object into an en-US string
            // Use the US locale for formatting (dot is decimal separator, comma is group separator)
            NumberFormat outputFormat = NumberFormat.getNumberInstance(Locale.US);
            enUsNumberString = outputFormat.format(number);


        } catch (ParseException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
            return enUsNumberString;
    }
}
